
package com.cmdli.dht;

import java.util.*;
import java.net.*;
import java.nio.charset.Charset;

import com.cmdli.dht.Node;
import com.cmdli.dht.RoutingTable;

class Request {
    String type;
    public Request(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}

class GetRequest extends Request {
    BitSet key;
    
    public GetRequest(BitSet key) {
        super("GET");
        this.key = key;
    }

    public String toString() {
        String keyString = new String(Base64.getEncoder().encode(key.toByteArray()));
        return super.toString() + " " + keyString;
    }
}

class RequestDecoder {

    public static Request decodeRequest(String requestString) {
        String[] args = requestString.split(" ");
        if (args.length < 1) {
            return null;
        }
        switch (args[0]) {
        case "GET":
            if (args.length < 2) {
                return null;
            }
            byte[] keyInBase64 = args[1].getBytes();
            byte[] decodedKeyBytes = Base64.getDecoder().decode(keyInBase64);
            BitSet key = BitSet.valueOf(decodedKeyBytes);
            return new GetRequest(key);
        default:
            return null;
        }
    }
}

public class DHT {

    public static final int K = 20;
    public static final int ID_LENGTH = 20;

    private Node currentNode;
    private RoutingTable routingTable;
    
    public DHT() {
        this.currentNode = new Node(DHT.randomID(ID_LENGTH), null, -1);
        this.routingTable = new RoutingTable(K, currentNode, ID_LENGTH);
    }

    public String get(BitSet key) {
        Queue<Node> nodesToProcess = new LinkedList<Node>(routingTable.getNodesNearID(key));
        List<Node> closestNodes = new ArrayList<Node>(nodesToProcess);
        while (!nodesToProcess.isEmpty()) {
            Node nextNode = nodesToProcess.remove();
            try {
                // Query node
                Socket socket = new Socket(nextNode.address(), nextNode.port()); 
                // Get response
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return "None";
    }

    public void put(BitSet key, String value) {
        
    }
    
    public static void main(String[] args) {
        Node currentNode = new Node(DHT.randomID(ID_LENGTH), null, -1);
        RoutingTable table = new RoutingTable(K, currentNode, ID_LENGTH);
        for (int i = 0; i < 1000000; i++) {
            table.addNode(new Node(randomID(ID_LENGTH), null, -1));
        }
        System.out.println(table);
    }

    public static BitSet randomID(int numBits) {
        Random random = new Random();
        BitSet id = new BitSet(numBits);
        for (int i = 0; i < numBits; i++) {
            if (random.nextBoolean()) {
                id.set(i);
            }
        }
        return id;
    }
}
