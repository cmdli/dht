
package com.cmdli.dht;

import java.util.*;
import java.net.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.io.*;

import com.google.gson.*;

import com.cmdli.dht.Node;
import com.cmdli.dht.RoutingTable;
import com.cmdli.dht.FetchProtocol;

public class DHT {

    public static final int K = 20;
    public static final int ID_LENGTH = 20;

    private Node currentNode;
    private RoutingTable routingTable;
    
    public DHT() {
        this.currentNode = new Node(DHT.randomID(ID_LENGTH), null, -1);
        this.routingTable = new RoutingTable(K, currentNode, ID_LENGTH);
    }

    public void addNode(Node node) {
        routingTable.addNode(node);
    }

    public void startServer(int port) {
        DHT dht = this;
        new Thread(new Runnable() {
                public void run() {
                    dht.serve(port);
                }
            }).start();
    }

    public void serve(int port) {
        try (
             ServerSocket serverSocket = new ServerSocket(port);
             ) {
            Socket clientSocket = serverSocket.accept();
            
            clientSocket.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    final static int MAX_FETCH_NODE_SET_SIZE = 20; // Size of closest nodes set when fetching
    public String get(BigInteger key) {
        System.out.println("Fetching: 0x" + key.toString(16));
        Comparator<Node> closeToFar = (Node n1, Node n2) -> n1.id().xor(key).compareTo(n2.id().xor(key));
        PriorityQueue<Node> closestNodes = new PriorityQueue<Node>(MAX_FETCH_NODE_SET_SIZE, closeToFar.reversed());
        PriorityQueue<Node> nodesToProcess = new PriorityQueue<Node>(MAX_FETCH_NODE_SET_SIZE, closeToFar);
        nodesToProcess.addAll(routingTable.getNodesNearID(key));
        closestNodes.addAll(nodesToProcess);
        HashSet<Node> visitedNodes = new HashSet<Node>(nodesToProcess);
        while (!nodesToProcess.isEmpty()) {
            Node nextNode = nodesToProcess.poll();
            System.out.println("Processing " + nextNode);
            List<Node> newNodes = new FetchProtocol().fetch(key, nextNode);
            if (newNodes != null) {
                for (Node node : newNodes) {
                    if (!visitedNodes.contains(node) &&
                        closeToFar.compare(node,closestNodes.peek()) < 0) {
                        closestNodes.add(node);
                        if (closestNodes.size() > MAX_FETCH_NODE_SET_SIZE)
                            closestNodes.poll();
                        nodesToProcess.add(node);
                    }
                }
            }
        }
        return "None";
    }

    public void put(BigInteger key, String value) {
        
    }

    public String toString() {
        return new StringBuilder()
            .append("Current Node: ")
            .append(currentNode)
            .append("\n")
            .append(routingTable)
            .toString();
    }

    // -------------------------------­-----------
    
    public static BigInteger randomID(int numBits) {
        Random random = new Random();
        return new BigInteger(numBits, random);
    }

    public static void main(String[] args) {
        DHT dht = new DHT();
        for (int i = 0; i < 1000000; i++) {
            dht.addNode(new Node(randomID(ID_LENGTH), null, -1));
        }
        System.out.println(dht);
        dht.get(randomID(ID_LENGTH));
    }
}
