
package com.cmdli.dht;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.cmdli.dht.Node;

public class RoutingTable {
    private int k;
    private List<List<Node>> kBuckets;
    private BigInteger currentKey;
    private int idLength;

    public RoutingTable(int k, BigInteger currentKey, int idLength) {
        this.currentKey = currentKey;
        this.kBuckets = new ArrayList<List<Node>>();
        this.idLength = idLength;
        for (int i = 0; i < idLength; i++) {
            this.kBuckets.add(new ArrayList<Node>());
        }
        this.k = k;
    }

    public void addNode(Node node) {
        // e.g. 011001 xor 010001 = 001000, bit length 4
        int bucketI = currentKey.xor(node.id()).bitLength()-1;
        if (bucketI < 0) { // Bits are exactly the same
            return;
        }
        List<Node> bucket = kBuckets.get(bucketI);
        if (bucket.size() < k) {
            bucket.add(node);
        } else {
            // TODO: Store extra nodes/cleanup nodes?
        }
    }

    public List<Node> getNodesNearID(BigInteger id, int limit) {
        return kBuckets.stream()
            .flatMap(List::stream)
            .sorted(Comparator.comparing(n -> n.id().xor(id)))
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<Node> allNodes() {
        return kBuckets.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Current key: ").append(currentKey).append("\n");
        int i = 0;
        for (List<Node> bucket : kBuckets) {
            builder.append("Bucket ").append(i++).append(": ");
            for (Node node : bucket) {
                builder.append(node).append(", ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
