
package com.cmdli.dht;

import java.util.*;
import java.util.stream.*;
import java.net.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.io.*;

import com.google.gson.*;

import com.cmdli.dht.Node;
import com.cmdli.dht.RoutingTable;
import com.cmdli.dht.FetchProtocol;
import com.cmdli.dht.messages.Message;

public class DHT {

    public static final int K = 20;
    public static final int ID_LENGTH = 20;

    private Node currentNode;
    private RoutingTable routingTable;
    private ServerSocket serverSocket;
    private volatile boolean serverRunning;
    private Thread serverThread;
    
    public DHT() {
        this.currentNode = new Node(DHT.randomID(ID_LENGTH), null, -1);
        this.routingTable = new RoutingTable(K, currentNode.id(), ID_LENGTH);
    }

    // Getters and Setters
    
    public Node currentNode() {
        return currentNode;
    }

    public void addNode(Node node) {
        routingTable.addNode(node);
    }

    public String toString() {
        return new StringBuilder()
            .append("Current Node: ")
            .append(currentNode)
            .append("\n")
            .append(routingTable)
            .toString();
    }

    // Server

    public void startServer(int port) {
        serverRunning = true;
        try {
            serverSocket = new ServerSocket(port);
            currentNode = new Node(currentNode.id(),
                                   InetAddress.getByName("127.0.0.1"),
                                   //serverSocket.getInetAddress(),
                                   serverSocket.getLocalPort());
        } catch (IOException e) {
            System.err.println(e);
        }
        DHT dht = this;
        serverThread = new Thread(new Runnable() {
                public void run() {
                    dht.serve();
                }
            });
        serverThread.start();
    }

    public void stopServer() {
        try {
            serverSocket.close();
            serverRunning = false;
            serverThread.join();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void serve() {
        Gson gson = new Gson();
        while (serverRunning) {
            try (
                 Connection conn = new Connection(serverSocket.accept());
                 ) {
                String initialMessage = conn.receive();
                Message message = gson.fromJson(initialMessage, Message.class);
                switch (message.type) {
                case "GetRequest":
                    new FetchProtocol(routingTable).respond(conn, initialMessage);
                    break;
                }
            } catch (IOException e) {
                //                System.err.println(e);
            }
        }
    }

    // Get and Put

    final static int MAX_FETCH_NODE_SET_SIZE = 20; // Size of closest nodes set when fetching
    public String get(BigInteger key) {
        System.out.println("Fetching: 0x" + key.toString(16));
        Comparator<Node> closeToFar = (Node n1, Node n2) -> n1.id().xor(key).compareTo(n2.id().xor(key));
        // Closest nodes found so far - ordered from farthest to closest
        PriorityQueue<Node> closestNodes = new PriorityQueue<Node>(MAX_FETCH_NODE_SET_SIZE, closeToFar.reversed());
        // Next nodes to query - ordered from closest to farthest
        PriorityQueue<Node> nodesToProcess = new PriorityQueue<Node>(MAX_FETCH_NODE_SET_SIZE, closeToFar);
        nodesToProcess.addAll(routingTable.getNodesNearID(key, K));
        closestNodes.addAll(nodesToProcess);
        HashSet<Node> visitedNodes = new HashSet<Node>(nodesToProcess);
        
        System.out.println("Starting nodes: " + nodesToProcess);
        int nodesProcessed = 0;
        while (!nodesToProcess.isEmpty()) {
            Node nextNode = nodesToProcess.poll();
            nodesProcessed++;
            System.out.println("Processing: " + nextNode);
            List<Node> fetchedNodes = new FetchProtocol().fetch(key, nextNode);
            if (fetchedNodes != null) {
                // Get unvisited nodes from response
                List<Node> newNodes = fetchedNodes.stream()
                    .filter(n -> !visitedNodes.contains(n))
                    .collect(Collectors.toList());
                visitedNodes.addAll(newNodes);
                
                // Merge new nodes into node set
                closestNodes.addAll(newNodes);
                while (closestNodes.size() > MAX_FETCH_NODE_SET_SIZE)
                    closestNodes.poll();
                
                // Query the newly added nodes
                List<Node> addedNodes = newNodes.stream()
                    .filter(n -> closestNodes.contains(n))
                    .collect(Collectors.toList());
                nodesToProcess.addAll(addedNodes);
                
                System.out.println("Added: " + addedNodes + " - " + (fetchedNodes.size() - addedNodes.size()) + " skipped");
                System.out.println("New node set: " + nodesToProcess);
            }
        }
        System.out.println("Nodes processed: " + nodesProcessed);
        return "None";
    }

    public void put(BigInteger key, String value) {
        
    }

    // -------------------------------­-----------
    
    public static BigInteger randomID(int numBits) {
        Random random = new Random();
        return new BigInteger(numBits, random);
    }

    public static void main(String[] args) {
        List<Node> nodes = new ArrayList<>();
        List<DHT> clients = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            DHT newDHT = new DHT();
            newDHT.startServer(0);
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
        Collections.sort(nodes, (n1, n2) -> (n1.id().xor(key).compareTo(n2.id().xor(key))));
        System.out.println("Nodes: " + nodes);
        DHT dht = clients.get(0);
        dht.get(DHT.randomID(DHT.ID_LENGTH));
        System.out.print("Stopping servers... ");
        for (DHT client : clients) {
            client.stopServer();
        }
        System.out.println("Done.");
    }
}
