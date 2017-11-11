
package com.cmdli.dht;

import java.util.*;
import java.math.BigInteger;

import com.google.gson.*;

import com.cmdli.dht.*;
import com.cmdli.dht.messages.*;

public class FindNodeProtocol {

    private static final Gson GSON = new Gson();
    
    private RoutingTable table;

    public FindNodeProtocol() {
        this(null);
    }
    
    public FindNodeProtocol(RoutingTable table) {
        this.table = table;
    }

    public FindNodeResponse fetch(BigInteger key, Node node) {
        Connection conn = new Connection().connect(node);
        conn.send(GSON.toJson(new FindNodeRequest(key)) + "\n");
        FindNodeResponse response = GSON.fromJson(conn.receive(), FindNodeResponse.class);
        conn.close();
        return response;
    }

    public void respond(Connection conn, String initialMessage) {
        FindNodeRequest request = GSON.fromJson(initialMessage, FindNodeRequest.class);
        if (request == null)
            return;
        List<Node> nodes = table.getNodesNearID(request.key, DHT.K);
        conn.send(GSON.toJson(new FindNodeResponse(nodes)) + "\n");
    }
}
