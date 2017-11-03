
package com.cmdli.dht;

import java.util.*;
import java.math.BigInteger;
import java.net.*;
import java.io.*;

import com.google.gson.*;

import com.cmdli.dht.messages.Message;
import com.cmdli.dht.DHT;

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

    private RoutingTable table;

    public FetchProtocol() {
        this(null);
    }
    
    public FetchProtocol(RoutingTable table) {
        this.table = table;
    }

    
    public List<Node> fetch(BigInteger key, Node node) {
        List<Node> result = null;
        System.out.printf("Connecting to %s:%d\n", node.address(), node.port());
        try (
             Socket socket = new Socket(node.address(), node.port());
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             ) {
            GetRequest request = new GetRequest(key);
            Gson gson = new Gson();
            String output = gson.toJson(request) + "\n";
            System.out.println("Sending - " + output);
            out.write(output);
            out.flush();
            String inputLine = in.readLine();
            System.out.println("Received - " + inputLine);
            GetResponse response = gson.fromJson(inputLine, GetResponse.class);
            if (response != null) {
                result = response.nodes;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return result;
    }

    public void respond(Socket socket, String initialMessage) {
        try (
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             ) {
            Gson gson = new Gson();
            GetRequest request = gson.fromJson(initialMessage, GetRequest.class);
            if (request == null)
                return;
            GetResponse response = new GetResponse(table.getNodesNearID(request.key));
            String responseString = gson.toJson(response);
            System.out.println("Sending - " + responseString);
            writer.write(gson.toJson(response));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Starting FetchProtocol...");

        if (args[0].equals("server")) {
            try (
                 ServerSocket serverSocket = new ServerSocket(0);
                 ) {
                System.out.println("Started server at port " + serverSocket.getLocalPort());
                Socket client = serverSocket.accept();
                System.out.printf("Connected to %s:%d\n",
                                  client.getInetAddress(),
                                  client.getPort());
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String initialMessage = reader.readLine();
                    Node currentNode = new Node(DHT.randomID(DHT.ID_LENGTH), null, -1);
                    RoutingTable table = new RoutingTable(DHT.K, currentNode, DHT.ID_LENGTH);
                    FetchProtocol serverProtocol = new FetchProtocol(table);
                    System.out.println("Received - " + initialMessage);
                    serverProtocol.respond(client, initialMessage);
                } finally {
                    client.close();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            int port = Integer.parseInt(args[0]);
            FetchProtocol protocol = new FetchProtocol();
            BigInteger key = DHT.randomID(DHT.ID_LENGTH);
            Node node = new Node(DHT.randomID(DHT.K), InetAddress.getLocalHost(), port);
            protocol.fetch(key, node);
        }
    }
}
