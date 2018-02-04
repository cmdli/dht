
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
    
    public Search(RoutingTable routingTable, BigInteger key, boolean findNodes) {
        this.result = null;
        this.routingTable = routingTable;
        this.key = key;
        this.findNodes = findNodes;
    }

    public SearchResult search() {
        if (result != null)
            return result;
        String value = null;
        Set<Node> nodes = new HashSet<>(routingTable.getNodesNearID(key, DHT.K));
        Set<Node> added = new HashSet<>(nodes);
        while (!nodes.isEmpty()) {
            Set<Node> newNodes = new HashSet<>();
            for (Node node : nodes) {
                SearchResult queryResult = queryNode(node);
                if (queryResult.value != null && findNodes) {
                    value = queryResult.value;
                    break;
                }
                // Add all closer nodes
                for (Node newNode : queryResult.nodes)
                    if (newNode.id().xor(key).compareTo(node.id().xor(key)) < 0)
                        newNodes.add(newNode);
                added.addAll(newNodes);
            }
            if (value != null)
                break;
            nodes = newNodes;
        }
        // Get K closest nodes found
        List<Node> closestNodes = new ArrayList<>(added);
        closestNodes.sort(Comparator.comparing(n -> n.id().xor(key)));
        closestNodes = closestNodes.subList(0,SEARCH_NODES);
        result = new SearchResult(closestNodes, value);
        return result;
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
