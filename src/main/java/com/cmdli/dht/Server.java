
package com.cmdli.dht;

import java.util.*;
import java.math.BigInteger;
import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import com.cmdli.dht.*;
import com.cmdli.dht.messages.*;
import com.cmdli.dht.protocols.*;

public class Server {

    public static final Gson GSON = new Gson();

    private RoutingTable table;
    private Map<String,String> storage;
    
    private int port;
    private volatile boolean serverRunning;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private Node currentNode;
    
    public Server(Node currentNode, RoutingTable table, Map<String, String> storage, int port) {
        this.table = table;
        this.storage = storage;
        this.port = port;
        this.currentNode = currentNode;
    }

    public boolean running() {
        return serverRunning;
    }
    
    public Node start() {
        serverRunning = true;
        try {
            serverSocket = new ServerSocket(port);
            currentNode = new Node(currentNode.id(),
                                   InetAddress.getByName("127.0.0.1"),
                                   serverSocket.getLocalPort());
            Server server = this;
            serverThread = new Thread(new Runnable() {
                    public void run() {
                        server.serve();
                    }
                });
            serverThread.start();
        } catch (IOException e) {
            System.err.println(e);
        }
        return currentNode;
    }

    public void stop() {
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
                String json = conn.receive();
                Message message = gson.fromJson(json, Message.class);
                if (message != null) {
                    switch (message.type) {
                    case "GetRequest":
                        new FetchProtocol(conn, table, storage).respond(json);
                        break;
                    case "FindNodeRequest":
                        new FindNodeProtocol(conn, table).respond(json);
                        break;
                    case "PutRequest":
                        System.out.printf("Adding to node %s\n", currentNode);
                        new PutProtocol(conn, storage).receive(json);
                        break;
                    }
                }
            } catch (IOException e) {
                // Main thread closed the socket
            }
        }
    }
}
