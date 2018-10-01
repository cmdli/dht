
package com.cmdli.dht;

import java.util.*;
import java.math.BigInteger;

import com.google.gson.*;

import com.cmdli.dht.protocols.*;

public class DHT {

    public static final Gson GSON = new Gson();
    public static final int K = 20;
    public static final int ID_LENGTH = 20;

    private Node currentNode;
    private RoutingTable routingTable;
    private HashMap<String, String> storage;
    private Server server;
    
    public DHT() {
        this.currentNode = new Node(DHT.randomID(ID_LENGTH), null, -1);
        this.routingTable = new RoutingTable(K, currentNode.id(), ID_LENGTH);
        this.storage = new HashMap<>();
    }

    // Getters and Setters
    
    public Node currentNode() {
        return currentNode;
    }

    public void addNode(Node node) {
        routingTable.addNode(node);
    }

    public Map<String,String> storage() {
        return storage;
    }

    @Override
    public String toString() {
        return "Current Node: " + currentNode + "\n" + routingTable;
    }

    // Server

    public void startServer() {
        server = new Server(currentNode, routingTable, storage, 0);
        Node serverNode = server.start();
        currentNode = new Node(currentNode.id(),
                               serverNode.address(),
                               serverNode.port());
    }

    public void stopServer() {
        server.stop();
    }

    // Get and Put

    public String get(BigInteger key) {
        SearchResult result = new Search(routingTable, key, true).search();
        return result.value;
    }

    public List<Node> put(BigInteger key, String value) {
        // Find closest nodes
        SearchResult result = new Search(routingTable, key, false).search();
        // Put key:value in those nodes
        for (Node node : result.nodes) {
            try (
                 Connection conn = new Connection().connect(node)
                 ) {
                new PutProtocol(conn).put(key, value);
            }
        }
        return result.nodes;
    }

    // Call this every once in awhile
    public void update() {
        for (Node node : routingTable.allNodes()) {
            try (Connection conn = new Connection().connect(node)) {
                GetPeerProtocol protocol = new GetPeerProtocol(conn,routingTable);
                List<Node> nodes = protocol.fetch();
                if (nodes != null) {
                    for (Node receivedNode : nodes) {
                        routingTable.addNode(receivedNode);
                    }
                } else {
                    System.err.println("Failed to receive nodes from " + node.id());
                }
            }
        }
    }

    // -------------------------------­-----------
    
    public static BigInteger randomID(int numBits) {
        Random random = new Random();
        return new BigInteger(numBits, random);
    }
}
