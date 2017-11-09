
package com.cmdli.dht;

import java.util.*;
import java.math.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class RoutingTableTest {

    public static Node createNode(String hexString) {
        return new Node(new BigInteger(hexString, 2), null, -1);
    }
    
    @Test
    public void testAddNode() {
        BigInteger currentKey = new BigInteger("0000",2);
        RoutingTable table = new RoutingTable(20, currentKey, 4);
        Node node = createNode("1000");
        table.addNode(node);
        List<Node> nodes = table.getNodesNearID(node.id(), 20);
        assertNotNull("Returned value should not be null", nodes);
        assertTrue("Added node not returned from table", nodes.contains(node));
    }

    @Test
    public void testGetNodesNearID() {
        // Test getting nodes out from the right bucket
        BigInteger currentKey = new BigInteger("0000",2);
        for (long i = 1; i < 16; i *= 2) {
            RoutingTable table = new RoutingTable(20, currentKey, 4);
            for (long j = i; j < i*2; j++) {
                table.addNode(new Node(BigInteger.valueOf(j), null, -1));
            }
            assertTrue("Bucket " + Long.toBinaryString(i) +
                       " doesn't contain " + i + " node(s)",
                       table.getNodesNearID(BigInteger.valueOf(i),20).size() == i);
            if (i > 1) {
                assertTrue("Bucket " + Long.toBinaryString(i/2) + " contains nodes",
                           table.getNodesNearID(BigInteger.valueOf(i/2),20).size() == 0);
            }
        }

        // Test the limit parameter
        RoutingTable table = new RoutingTable(20, currentKey, 4);
        for (int i = 1; i < 4; i++) {
            table.addNode(new Node(BigInteger.valueOf(i), null, -1));
        }
        // Bucket 1 has 1 node, Bucket 2 has two nodes
        assertTrue("Table did not limit reponse to 1",
                   table.getNodesNearID(BigInteger.valueOf(1), 20).size() == 1);
        assertTrue("Table did not limit response to 2",
                   table.getNodesNearID(BigInteger.valueOf(2), 2).size() == 2);
        assertTrue("Table did not give all nodes",
                   table.getNodesNearID(BigInteger.valueOf(2), 3).size() == 3);
    }
    
}
