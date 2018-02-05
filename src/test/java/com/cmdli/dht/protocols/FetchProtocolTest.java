
package com.cmdli.dht.protocols;

import java.util.*;
import java.math.BigInteger;

import com.google.gson.*;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

import com.cmdli.dht.*;
import com.cmdli.dht.protocols.*;
import com.cmdli.dht.messages.*;

public class FetchProtocolTest {

    public static final Gson GSON = new Gson();
    
    @Test
    public void testFetch() {
        FakeConnection conn = new FakeConnection();
        FetchProtocol protocol = new FetchProtocol(conn);
        BigInteger key = BigInteger.valueOf(5);
        GetResponse response = new GetResponse(new ArrayList<>(), "value");
        
        conn.addReceivedMessage(GSON.toJson(response));
        GetResponse returnedResponse = protocol.fetch(key);
        assertEquals(returnedResponse.nodes, response.nodes);
        assertEquals(returnedResponse.value, response.value);
        List<String> messages = conn.getSentMessages();
        assertTrue(messages.size() == 1);
        GetRequest request = GSON.fromJson(messages.get(0), GetRequest.class);
        assertNotNull(request);
        assertEquals(request.key, key);
    }

    @Test
    public void testRespond() {
        FakeConnection conn = new FakeConnection();
        HashMap<String,String> storage = new HashMap<>();
        BigInteger key = BigInteger.valueOf(5);
        storage.put(key.toString(16), "value");
        RoutingTable table = new RoutingTable(20,BigInteger.valueOf(0),4);
        Node node = new Node(key,null,-1);
        table.addNode(node);
        GetRequest request = new GetRequest(key);
        new FetchProtocol(conn,table,storage).respond(GSON.toJson(request));
        List<String> messages = conn.getSentMessages();
        assertTrue(messages.size() == 1);
        GetResponse response = GSON.fromJson(messages.get(0),GetResponse.class);
        assertNotNull(response);
        assertEquals(response.nodes, Arrays.asList(node));
        assertEquals(response.value, "value");
    }
}
