
package com.cmdli.dht;

import java.util.List;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Arrays;

import com.cmdli.dht.Node;

public class RoutingTable {
    private int k;
    private List<List<Node>> kBuckets;
    private Node currentNode;
    private int idLength;

    public RoutingTable(int k, Node currentNode, int idLength) {
        this.currentNode = currentNode;
        this.kBuckets = new ArrayList<List<Node>>();
        this.idLength = idLength;
        for (int i = 0; i < idLength; i++) {
            this.kBuckets.add(new ArrayList<Node>());
        }
        this.k = k;
    }

    public void addNode(Node node) {
        int bucketI = firstDifferingBit(node.id(), currentNode.id());
        if (bucketI == -1) { // Bits are exactly the same
            return;
        }

        List<Node> bucket = kBuckets.get(bucketI);
        if (bucket.size() < k) {
            bucket.add(node);
        } else {
            // TODO: Store extra nodes/cleanup nodes?
        }
    }

    public List<Node> getNodesNearID(BitSet id) {
        int bucketI = firstDifferingBit(id, currentNode.id());
        if (bucketI == -1)  {
            return Arrays.asList(currentNode);
        }
        return kBuckets.get(bucketI);
    }

    private int firstDifferingBit(BitSet id1, BitSet id2) {
        BitSet difference = (BitSet)id1.clone();
        difference.xor(id2);
        return difference.nextSetBit(0);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Current Node: ");
        builder.append(currentNode);
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
