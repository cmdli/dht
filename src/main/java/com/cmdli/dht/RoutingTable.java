
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
        // Key: 0000
        // 4: 1xxx
        // 3: 01xx
        // 2: 001x
        // 1: 0001
        // TODO: worth limiting it to strictly limit?
        return kBuckets.stream()
            .flatMap(List::stream)
            .sorted(Comparator.comparing(n -> n.id().xor(id)))
            .limit(limit)
            .collect(Collectors.toList());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Current key: ");
        builder.append(currentKey);
        builder.append("\n");
        int i = 0;
        for (List<Node> bucket : kBuckets) {
            builder.append("Bucket ");
            builder.append(i++);
            builder.append(": ");
            for (Node node : bucket) {
                builder.append(node);
                builder.append(", ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
