
package com.cmdli.dht.protocols;

import java.util.*;
import java.math.BigInteger;

import com.google.gson.*;

import com.cmdli.dht.*;
import com.cmdli.dht.messages.*;

public class PutProtocol {

    public static final Gson GSON = new Gson();

    private Connection conn;
    private Map<String,String> storage;

    public PutProtocol(Connection conn) {
        this(conn, null);
    }
    
    public PutProtocol(Connection conn, Map<String,String> storage) {
        this.conn = conn;
        this.storage = storage;
    }

    public void put(BigInteger key, String value) {
        conn.send(GSON.toJson(new PutRequest(key.toString(16),value)));
    }

    public void receive(String json) {
        PutRequest request = GSON.fromJson(json, PutRequest.class);
        if (request == null)
            return;
        System.out.println("Putting " + request.key + ":" + request.value);
        storage.put(request.key, request.value);
    }
}
