
package com.cmdli.dht.protocols;

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
    private Connection conn;

    public FetchProtocol(Connection conn) {
        this(conn, null, null);
    }
    
    public FetchProtocol(Connection conn,
                         RoutingTable table,
                         HashMap<String, String> storage) {
        this.conn = conn;
        this.table = table;
        this.storage = storage;
    }
    
    public GetResponse fetch(BigInteger key) {
        conn.send(GSON.toJson(new GetRequest(key)) + "\n");
        GetResponse response = GSON.fromJson(conn.receive(), GetResponse.class);
        return response;
    }

    public void respond(Message message) {
        if (!(message instanceof GetRequest))
            return;
        GetRequest request = (GetRequest)message;
        List<Node> nodes = table.getNodesNearID(request.key, DHT.K);
        String value = storage.get(request.key.toString(16));
        conn.send(GSON.toJson(new GetResponse(nodes,value)) + "\n");
    }
}
