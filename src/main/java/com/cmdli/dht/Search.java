
package com.cmdli.dht;

import java.util.*;
import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.stream.*;

import com.cmdli.dht.*;
import com.cmdli.dht.protocols.*;
import com.cmdli.dht.messages.*;

public class Search {

    private final static int SEARCH_NODES = 10; // Nodes to return from search
    
    private SearchResult result;
    private RoutingTable routingTable;
    private BigInteger key;
    private boolean findNodes; // Ignore result and just find closest nodes
    private Set<Node> closestNodes;
    private Set<Node> allNodes;
    
    public Search(RoutingTable routingTable, BigInteger key, boolean findNodes) {
        this.result = null;
        this.routingTable = routingTable;
        this.key = key;
        this.findNodes = findNodes;
        this.closestNodes = new HashSet<>(routingTable.getNodesNearID(key, DHT.K));
        this.allNodes = new HashSet<>(this.closestNodes);
    }

    public SearchResult search() {
        while (result == null)
            step();
        return result;
    }

    private void step() {
        String value = null;
        Set<Node> newNodes = new HashSet<>();
        for (Node node : this.closestNodes) {
            SearchResult queryResult = queryNode(node);
            if (queryResult.value != null && findNodes) {
                value = queryResult.value;
                break;
            }
            // Add all closer nodes
            for (Node newNode : queryResult.nodes)
                if (newNode.id().xor(key).compareTo(node.id().xor(key)) < 0)
                    newNodes.add(newNode);
            allNodes.addAll(newNodes);
        }
        closestNodes = newNodes;
        if (value != null || newNodes.isEmpty()) {
            // Get K closest nodes found
            List<Node> closestNodes = new ArrayList<>(this.allNodes);
            closestNodes.sort(Comparator.comparing(n -> n.id().xor(key)));
            closestNodes = closestNodes.subList(0,SEARCH_NODES);
            result = new SearchResult(closestNodes, value);
        }
    }

    private SearchResult queryNode(Node node) {
        try (
             Connection conn = new Connection().connect(node);
             ) {
            if (findNodes) {
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
