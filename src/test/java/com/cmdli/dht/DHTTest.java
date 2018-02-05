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
        List<Node> nodes = new ArrayList<>();
        List<DHT> clients = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            DHT newDHT = new DHT();
            newDHT.startServer();
            clients.add(newDHT);
        }
        for (DHT client1 : clients) {
            nodes.add(client1.currentNode());
            for (DHT client2 : clients) {
                if (!client1.currentNode().equals(client2.currentNode()))
                    client1.addNode(client2.currentNode());
            }
        }

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
