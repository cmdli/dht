
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

public class FindNodeProtocolTest {

    public static final Gson GSON = new Gson();
    
    @Test
    public void testFetch() {
        FakeConnection conn = new FakeConnection();
        Node node = new Node(BigInteger.valueOf(5),null, -1);
        FindNodeResponse response = new FindNodeResponse(Arrays.asList(node));
        conn.addReceivedMessage(GSON.toJson(response));
        assertEquals(response,
                     new FindNodeProtocol(conn)
                     .fetch(BigInteger.valueOf(5)));
        List<String> messages = conn.getSentMessages();
        assertTrue(messages.size() == 1);
        assertEquals(new FindNodeRequest(BigInteger.valueOf(5)),
                     GSON.fromJson(messages.get(0),FindNodeRequest.class));
    }

    @Test
    public void testRespond() {
        Node node = new Node(BigInteger.valueOf(5),null,-1);
        RoutingTable table = new RoutingTable(20,BigInteger.valueOf(0),4);
        table.addNode(node);
        FakeConnection conn = new FakeConnection();
        new FindNodeProtocol(conn,table)
            .respond(GSON.toJson(new FindNodeRequest(BigInteger.valueOf(5))));
        List<String> messages = conn.getSentMessages();
        assertTrue(messages.size() == 1);
        assertEquals(new FindNodeResponse(Arrays.asList(node)),
                     GSON.fromJson(messages.get(0), FindNodeResponse.class));
        
    }
}
