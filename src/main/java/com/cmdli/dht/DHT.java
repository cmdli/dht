
package com.cmdli.dht;

import java.util.*;
import java.util.stream.*;
import java.net.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.io.*;

import com.google.gson.*;

import com.cmdli.dht.*;
import com.cmdli.dht.messages.*;
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

    public void put(BigInteger key, String value) {
        // Find closest nodes
        // Put key:value in those nodes
        SearchResult result = new Search(routingTable, key, false).search();
        for (Node node : result.nodes) {
            try (
                 Connection conn = new Connection().connect(node)
                 ) {
                new PutProtocol(conn).put(key, value);
            }
        }
    }

    // -------------------------------­-----------
    
    public static BigInteger randomID(int numBits) {
        Random random = new Random();
        return new BigInteger(numBits, random);
    }

    public static void main(String[] args) throws Exception {
        List<Node> nodes = new ArrayList<>();
        List<DHT> clients = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            DHT newDHT = new DHT();
            newDHT.startServer();
            clients.add(newDHT);
        }
        for (DHT client1 : clients) {
            nodes.add(client1.currentNode());
            for (DHT client2 : clients) {
                client1.addNode(client2.currentNode());
            }
        }
        BigInteger key = DHT.randomID(DHT.ID_LENGTH);
        System.out.println("Key: 0x" + key.toString(16));
        nodes.sort((n1, n2) -> (n1.id().xor(key).compareTo(n2.id().xor(key))));
        System.out.println("Nodes: " + nodes);
        DHT dht = clients.get(0);
        dht.put(key,"value");
        Thread.sleep(500);
        System.out.println("Fetch result: " + dht.get(key));
        
        System.out.print("Stopping servers... ");
        for (DHT client : clients) {
            client.stopServer();
        }
        System.out.println("Done.");
    }
}
