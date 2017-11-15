
package com.cmdli.dht;

import java.util.*;
import java.util.stream.*;
import java.math.*;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class RoutingTableTest {

    public static Node createNode(String hexString) {
        return new Node(new BigInteger(hexString, 2), null, -1);
    }

    public static Node createNode(long key) {
        return new Node(BigInteger.valueOf(key), null, -1);
    }

    public static <T> Set<T> set(Collection<T> items) {
        return new HashSet<T>(items);
    }
    
    public static <T> Set<T> set(T... items) {
        return new HashSet<T>(Arrays.asList(items));
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
        BigInteger currentKey = new BigInteger("0000", 2);
        RoutingTable table = new RoutingTable(20, currentKey, 4);
        for (int i = 0; i < 16; i++) {
            table.addNode(createNode(i));
        }
        Set<Node> nodes = set(table.getNodesNearID(BigInteger.valueOf(5), 3));
        assertEquals(nodes.size(), 3);
        assertEquals(nodes, set(createNode(5), createNode(7), createNode(4)));
    }

    @Test
    public void testGetNodesLimit() {
        BigInteger currentKey = new BigInteger("0000", 2);
        RoutingTable table = new RoutingTable(20, currentKey, 4);
        for (int i = 1; i < 4; i++) {
            table.addNode(createNode(i));
        }
        List<Node> nodes = table.getNodesNearID(BigInteger.valueOf(2), 2);
        assertEquals(nodes.size(), 2);
        nodes = table.getNodesNearID(BigInteger.valueOf(2), 3);
        assertEquals(nodes.size(), 3);
    }

    @Test
    public void testTableKLimit() {
        BigInteger currentKey = new BigInteger("0000", 2);
        RoutingTable table = new RoutingTable(5, currentKey, 4);
        for (int i = 8; i < 16; i++) {
            table.addNode(createNode(i));
        }
        // Bucket 4 should have 5 nodes only
        Set<Node> nodes = set(table.getNodesNearID(BigInteger.valueOf(15), 20));
        assertEquals(nodes.size(), 5);
        assertEquals(nodes, set(createNode(8),createNode(9),createNode(10),
                                createNode(11),createNode(12)));
    }
    
}
