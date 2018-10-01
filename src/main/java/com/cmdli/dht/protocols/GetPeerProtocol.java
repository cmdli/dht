package com.cmdli.dht.protocols;

import com.cmdli.dht.Connection;
import com.cmdli.dht.Node;
import com.cmdli.dht.RoutingTable;
import com.cmdli.dht.messages.GetPeerRequest;
import com.cmdli.dht.messages.GetPeerResponse;
import com.google.gson.Gson;

import java.util.List;

public class GetPeerProtocol {

    public static final String NAME = "GET_PEER";
    private static final Gson GSON = new Gson();

    private Connection conn;
    private RoutingTable table;

    public GetPeerProtocol(Connection conn, RoutingTable table) {
        this.conn = conn;
        this.table = table;
    }

    public void respond(String json) {
        GetPeerResponse response = new GetPeerResponse(table.allNodes());
        conn.send(GSON.toJson(response));
    }

    public List<Node> fetch() {
        conn.send(GSON.toJson(new GetPeerRequest()));
        String json = conn.receive();
        GetPeerResponse response = GSON.fromJson(json, GetPeerResponse.class);
        if (response == null) System.out.println(json);
        if (response.nodes == null) System.out.println(json + " - " + response);
        return response.nodes;
    }
}
