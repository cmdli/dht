
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
        Thread serverThread = new Thread(new Runnable() {
                public void run() {
                    dht.serve(port);
                }
            });
        serverThread.start();
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

    final static int MAX_FETCH_NODE_SET_SIZE = 20; // Number of close nodes to keep when fetching
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
            System.out.println(nextNode);
            /*List<Node> newNodes = null;
            try (
                 Socket socket = new Socket(nextNode.address(), nextNode.port());
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 ) {
                // Query node
                Gson gson = new Gson();
                GetRequest getRequest = new GetRequest(key);
                String getRequestStr = gson.toJson(getRequest);
                System.out.println("Request to " + nextNode + ": " + getRequestStr);
                out.write(gson.toJson(getRequest));

                // Get response
                String response = in.readLine();
                System.out.println("Response: " + response);
                GetResponse getResponse = gson.fromJson(response, GetResponse.class);
                if (getResponse != null) {
                    newNodes = getResponse.nodes;
                }
            } catch (Exception e) {
                System.err.println(e);
            }
            /*for (Node node : getResponse.nodes) {
                // Add nodes if they are closer than the current ones
                if (!visitedNodes.contains(node) &&
                    (closestNodes.size() < maxNodeSetSize ||
                     closestNodes.comparator().compare(closestNodes.peek(),node) < 0)) {
                    closestNodes.add(node);
                    if (closestNodes.size() > maxNodeSetSize)
                        closestNodes.poll();
                    nodesToProcess.add(node);
                    visitedNodes.add(node);
                }
                }*/
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
