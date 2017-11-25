
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
        System.out.println("Fetching: 0x" + key.toString(16));
        Comparator<Node> closeToFar = (Node n1, Node n2) -> n1.id().xor(key).compareTo(n2.id().xor(key));
        // Closest nodes found so far - ordered from farthest to closest
        PriorityQueue<Node> closestNodes = new PriorityQueue<>(MAX_FETCH_NODE_SET_SIZE, closeToFar.reversed());
        // Next nodes to query - ordered from closest to farthest
        PriorityQueue<Node> nodesToProcess = new PriorityQueue<>(MAX_FETCH_NODE_SET_SIZE, closeToFar);
        nodesToProcess.addAll(routingTable.getNodesNearID(key, DHT.K));
        closestNodes.addAll(nodesToProcess);
        HashSet<Node> processedNodes = new HashSet<>(nodesToProcess);
        
        System.out.println("Starting nodes: " + nodesToProcess);
        int nodesProcessed = 0;
        String value = null;
        while (!nodesToProcess.isEmpty()) {
            Node nextNode = nodesToProcess.poll();
            nodesProcessed++;
            System.out.println("Processing: " + nextNode);
            SearchResult queryResult = queryNode(nextNode);
            if (queryResult.value != null) {
                value = queryResult.value;
                break;
            }
            List<Node> fetchedNodes = queryResult.nodes;
            if (fetchedNodes != null) {
                // Get unprocessed nodes from response
                List<Node> newNodes = fetchedNodes.stream()
                    .filter(n -> !processedNodes.contains(n))
                    .collect(Collectors.toList());
                processedNodes.addAll(newNodes);
                
                // Merge new nodes into node set
                closestNodes.addAll(newNodes);
                while (closestNodes.size() > MAX_FETCH_NODE_SET_SIZE)
                    closestNodes.poll();
                
                // Add the newly added nodes for processing
                List<Node> addedNodes = newNodes.stream()
                    .filter(n -> closestNodes.contains(n))
                    .collect(Collectors.toList());
                nodesToProcess.addAll(addedNodes);
                
                System.out.println("Added: " + addedNodes + " - " +
                                   (fetchedNodes.size() - addedNodes.size()) +
                                   " skipped");
                //System.out.println("New node set: " + nodesToProcess);
            }
        }
        System.out.println("Nodes processed: " + nodesProcessed);
        result = new SearchResult(new ArrayList<>(closestNodes), value);
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
            System.out.println(e);
        }
        return new SearchResult(null,null);
    }
    
}
