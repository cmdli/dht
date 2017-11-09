
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
        BigInteger currentKey = new BigInteger("0000",2);
        RoutingTable table = new RoutingTable(20, currentKey, 4);
        for (long i = 0; i < 16; i++) {
            table.addNode(new Node(BigInteger.valueOf(i), null, -1));
        }
        assertTrue("First bucket doesn't contain 1 node",
                   table.getNodesNearID(BigInteger.valueOf(1l),1).size() == 1);
        assertTrue("Second bucket doesn't contain 2 nodes",
                   table.getNodesNearID(BigInteger.valueOf(2l),2).size() == 2);
        assertTrue("Third bucket doesn't contain 4 nodes",
                   table.getNodesNearID(BigInteger.valueOf(4l),4).size() == 4);
        assertTrue("Fourth bucket doesn't contain 8 nodes",
                   table.getNodesNearID(BigInteger.valueOf(8l),8).size() == 8);
    }
    
}
