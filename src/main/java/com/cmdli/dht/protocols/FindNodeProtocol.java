
package com.cmdli.dht.protocols;

import java.util.*;
import java.math.BigInteger;

import com.google.gson.*;

import com.cmdli.dht.*;
import com.cmdli.dht.messages.*;

public class FindNodeProtocol {

    private static final Gson GSON = new Gson();

    private Connection conn;
    private RoutingTable table;

    public FindNodeProtocol(Connection conn) {
        this(conn, null);
    }
    
    public FindNodeProtocol(Connection conn, RoutingTable table) {
        this.conn = conn;
        this.table = table;
    }

    public FindNodeResponse fetch(BigInteger key) {
        conn.send(GSON.toJson(new FindNodeRequest(key)) + "\n");
        return GSON.fromJson(conn.receive(), FindNodeResponse.class);
    }

    public void respond(Message message) {
        if (!(message instanceof FindNodeRequest))
            return;
        FindNodeRequest request = (FindNodeRequest)message;
        List<Node> nodes = table.getNodesNearID(request.key, DHT.K);
        conn.send(GSON.toJson(new FindNodeResponse(nodes)) + "\n");
    }
}
