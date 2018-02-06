
package com.cmdli.dht.protocols;

import java.util.*;
import java.math.BigInteger;

import com.google.gson.*;

import com.cmdli.dht.messages.*;
import com.cmdli.dht.*;

public class GetProtocol {

    private static final Gson GSON = new Gson();
    public static final String NAME = "GET";
    
    private RoutingTable table;
    private Map<String,String> storage;
    private Connection conn;

    public GetProtocol(Connection conn) {
        this(conn, null, null);
    }
    
    public GetProtocol(Connection conn,
                       RoutingTable table,
                       Map<String, String> storage) {
        this.conn = conn;
        this.table = table;
        this.storage = storage;
    }
    
    public GetResponse fetch(BigInteger key) {
        conn.send(GSON.toJson(new GetRequest(key)) + "\n");
        GetResponse response = GSON.fromJson(conn.receive(), GetResponse.class);
        return response;
    }

    public void respond(String json) {
        GetRequest request = GSON.fromJson(json, GetRequest.class);
        if (request == null)
            return;
        List<Node> nodes = table.getNodesNearID(request.key, DHT.K);
        String value = storage.get(request.key.toString(16));
        conn.send(GSON.toJson(new GetResponse(nodes,value)) + "\n");
    }
}
