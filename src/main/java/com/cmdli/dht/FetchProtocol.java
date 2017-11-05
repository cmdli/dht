
package com.cmdli.dht;

import java.util.*;
import java.math.BigInteger;
import java.net.*;
import java.io.*;

import com.google.gson.*;

import com.cmdli.dht.messages.Message;
import com.cmdli.dht.DHT;
import com.cmdli.dht.Connection;

class GetRequest extends Message {
    BigInteger key;
    
    public GetRequest(BigInteger key) {
        super("GetRequest");
        this.key = key;
    }
}

class GetResponse extends Message {
    List<Node> nodes;
    public GetResponse(List<Node> nodes) {
        super("GetResponse");
        this.nodes = nodes;
    }
}

public class FetchProtocol {

    public static final Gson GSON = new Gson();
    
    private RoutingTable table;

    public FetchProtocol() {
        this(null);
    }
    
    public FetchProtocol(RoutingTable table) {
        this.table = table;
    }

    
    public List<Node> fetch(BigInteger key, Node node) {
        Connection connection = new Connection().connect(node);
        String output = GSON.toJson(new GetRequest(key)) + "\n";
        connection.send(output);
        GetResponse response = GSON.fromJson(connection.receive(), GetResponse.class);
        connection.close();
        return response != null ? response.nodes : null;
    }

    public void respond(Connection connection, String initialMessage) {
        GetRequest request = GSON.fromJson(initialMessage, GetRequest.class);
        if (request == null)
            return;
        GetResponse response = new GetResponse(table.getNodesNearID(request.key));
        connection.send(GSON.toJson(response));
    }

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Starting FetchProtocol...");

        if (args[0].equals("server")) {
            Node currentNode = new Node(DHT.randomID(DHT.ID_LENGTH), null, -1);
            RoutingTable table = new RoutingTable(DHT.K, currentNode, DHT.ID_LENGTH);
            for (int i = 0; i < 10; i++) {
                table.addNode(new Node(DHT.randomID(DHT.ID_LENGTH), null, -1));
            }
            System.out.println(table);
            try (
                 ServerSocket serverSocket = new ServerSocket(0);
                 ) {
                System.out.println("Started server at port " + serverSocket.getLocalPort());
                try (Connection connection = new Connection(serverSocket.accept());) {
                    System.out.printf("Connected to %s:%d\n",
                                      connection.address(),
                                      connection.port());
                    String initialMessage = connection.receive();
                    FetchProtocol serverProtocol = new FetchProtocol(table);
                    serverProtocol.respond(connection, initialMessage);
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            int port = Integer.parseInt(args[0]);
            BigInteger key = DHT.randomID(DHT.ID_LENGTH);
            Node node = new Node(DHT.randomID(DHT.K),
                                 InetAddress.getLocalHost(),
                                 port);
            FetchProtocol protocol = new FetchProtocol();
            protocol.fetch(key, node);
        }
    }
}
