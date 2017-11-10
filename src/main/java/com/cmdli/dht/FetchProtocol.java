
package com.cmdli.dht;

import java.util.*;
import java.math.BigInteger;
import java.net.*;
import java.io.*;

import com.google.gson.*;

import com.cmdli.dht.messages.*;
import com.cmdli.dht.*;

public class FetchProtocol {

    public static final Gson GSON = new Gson();
    
    private RoutingTable table;
    private HashMap<String,String> storage;

    public FetchProtocol() {
        this(null, null);
    }
    
    public FetchProtocol(RoutingTable table, HashMap<String, String> storage) {
        this.table = table;
        this.storage = storage;
    }
    
    public GetResponse fetch(BigInteger key, Node node) {
        Connection connection = new Connection().connect(node);
        connection.send(GSON.toJson(new GetRequest(key)) + "\n");
        GetResponse response = GSON.fromJson(connection.receive(), GetResponse.class);
        connection.close();
        return response;
    }

    public void respond(Connection connection, String initialMessage) {
        GetRequest request = GSON.fromJson(initialMessage, GetRequest.class);
        if (request == null)
            return;
        List<Node> nodes = table.getNodesNearID(request.key, DHT.K);
        String value = storage.get(request.key.toString(16));
        GetResponse response = new GetResponse(nodes,value);
        connection.send(GSON.toJson(response) + "\n");
    }

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Starting FetchProtocol...");

        if (args[0].equals("server")) {
            Node currentNode = new Node(DHT.randomID(DHT.ID_LENGTH), null, -1);
            RoutingTable table = new RoutingTable(DHT.K, currentNode.id(), DHT.ID_LENGTH);
            for (int i = 0; i < 10; i++) {
                table.addNode(new Node(DHT.randomID(DHT.ID_LENGTH), null, -1));
            }
            System.out.println(table);
            try (
                 ServerSocket serverSocket = new ServerSocket(0);
                 ) {
                System.out.println("Started server at port " + serverSocket.getLocalPort());
                while (true) {
                    try (Connection connection = new Connection(serverSocket.accept());) {
                        System.out.printf("Connected to %s:%d\n",
                                          connection.address(),
                                          connection.port());
                        String initialMessage = connection.receive();
                        FetchProtocol serverProtocol = new FetchProtocol(table, new HashMap<>());
                        serverProtocol.respond(connection, initialMessage);
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            long start = System.currentTimeMillis();
            int port = Integer.parseInt(args[0]);
            BigInteger key = new BigInteger(args[1]);
            Node node = new Node(DHT.randomID(DHT.K),
                                 InetAddress.getByName("127.0.0.1"),
                                 port);
            FetchProtocol protocol = new FetchProtocol();
            protocol.fetch(key, node);
            System.out.println("Time: " + (System.currentTimeMillis() - start));
        }
    }
}
