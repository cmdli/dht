
package com.cmdli.dht.protocols;

import java.util.*;
import java.math.BigInteger;

import com.google.gson.*;

import com.cmdli.dht.*;
import com.cmdli.dht.messages.*;

public class PutProtocol {

    public static final Gson GSON = new Gson();

    private HashMap<String,String> storage;

    public PutProtocol() {
        this(null);
    }
    
    public PutProtocol(HashMap<String,String> storage) {
        this.storage = storage;
    }

    public void put(BigInteger key, String value, Node node) {
        Connection conn = new Connection().connect(node);
        conn.send(GSON.toJson(new PutRequest(key.toString(16),value)));
        conn.close();
    }

    public void receive(Connection conn, String initialMessage) {
        PutRequest request = GSON.fromJson(initialMessage, PutRequest.class);
        if (request != null) {
            System.out.println("Putting " + request.key + ":" + request.value);
            storage.put(request.key, request.value);
        }
    }
}
