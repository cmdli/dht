
package com.cmdli.dht;

import java.util.*;
import java.math.BigInteger;
import java.util.stream.*;

import com.cmdli.dht.*;
import com.cmdli.dht.protocols.*;
import com.cmdli.dht.messages.*;

public class Search {

    private final static int MAX_FETCH_NODE_SET_SIZE = 20; // Size of closest nodes set when fetching
    
    private SearchResult result;
    private RoutingTable routingTable;
    private BigInteger key;
    private boolean findNode;
    
    public Search(RoutingTable routingTable, BigInteger key, boolean findNode) {
        this.result = null;
        this.routingTable = routingTable;
        this.key = key;
        this.findNode = findNode;
    }

    public SearchResult search() {
        if (result != null)
            return result;
        String value = null;
        Set<Node> nodes = new HashSet<>(routingTable.getNodesNearID(key, DHT.K));
        while (!nodes.isEmpty()) {
            Set<Node> newNodes = new HashSet<>();
            for (Node node : nodes) {
                SearchResult queryResult = queryNode(node);
                if (queryResult.value != null) {
                    value = queryResult.value;
                    break;
                }
                newNodes.addAll(queryResult.nodes);
            }
            if (value != null)
                break;
            nodes = newNodes;
        }
        result = new SearchResult(new ArrayList<>(nodes), value);
        return result;
    }

    private SearchResult queryNode(Node node) {
        try (
             Connection conn = new Connection().connect(node);
             ) {
            if (findNode) {
                GetResponse fetchResponse = new FetchProtocol(conn).fetch(key);
                if (fetchResponse != null)
                    return new SearchResult(fetchResponse.nodes, fetchResponse.value);
            } else {
                FindNodeResponse response = new FindNodeProtocol(conn).fetch(key);
                if (response != null)
                    return new SearchResult(response.nodes, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SearchResult(null,null);
    }
    
}
