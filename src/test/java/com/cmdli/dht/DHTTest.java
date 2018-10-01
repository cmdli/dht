package com.cmdli.dht;

import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DHTTest {

    @Test
    public void DHTEndToEndTest() throws InterruptedException {
        int numClients = 1000;
        System.out.print("Creating " + numClients + " clients...");
        List<Node> nodes = new ArrayList<>();
        List<DHT> clients = new ArrayList<>();
        for (int i = 0; i < numClients; i++) {
            DHT newDHT = new DHT();
            newDHT.startServer();
            clients.add(newDHT);
        }
        // Add clients to node list and add next client as peer
        for (DHT dht : clients) {
            nodes.add(dht.currentNode());
            //dht.addNode(clients.get((i+1)%clients.size()).currentNode());
            for (DHT otherDHT : clients) {
                dht.addNode(otherDHT.currentNode());
            }
        }
        System.out.println(" done.");

        // Run peer protocol 10 times for each node
        /*System.out.print("Running peer protocol...");
        for (int i = 0; i < 10; i++) {
            for (DHT client : clients) {
                client.update();
            }
        }
        System.out.println(" done.");*/

        for (int i = 0; i < 3; i++) {
            BigInteger key = DHT.randomID(DHT.ID_LENGTH);
            String value = Integer.toString(i);
            System.out.println("Testing key: 0x" + key.toString(16));
            nodes.sort((n1, n2) -> (n1.id().xor(key).compareTo(n2.id().xor(key))));
            System.out.println("Sorted nodes: " + nodes);

            DHT dht = clients.get(0);
            System.out.println("Putting 0x" + key.toString(16) + ":" + value + "...");
            List<Node> storageNodes = dht.put(key, value);
            System.out.println("Stored in nodes " + storageNodes);
            System.out.println("Expected in nodes " + nodes.subList(0,storageNodes.size()));
            assertTrue(new HashSet<>(storageNodes).equals(new HashSet<>(nodes.subList(0,storageNodes.size()))));
            System.out.println("Nodes match!");
            Thread.sleep(500);

            System.out.println("Starting fetch...");
            String result = dht.get(key);
            System.out.println("Fetched (" + result + "), expected (" + value + ")");
            assertTrue(result.equals(value));
        }

        System.out.print("Stopping servers... ");
        for (DHT client : clients) {
            client.stopServer();
        }
        System.out.println("Done.");
    }
}
