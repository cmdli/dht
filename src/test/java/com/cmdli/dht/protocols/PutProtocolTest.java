
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

public class PutProtocolTest {

    public static final Gson GSON = new Gson();

    @Test
    public void testPut() {
        FakeConnection conn = new FakeConnection();
        PutProtocol protocol = new PutProtocol(conn);
        protocol.put(BigInteger.valueOf(5), "value");
        List<String> messages = conn.getSentMessages();
        assertTrue(messages.size() == 1);
        PutRequest request = GSON.fromJson(messages.get(0), PutRequest.class);
        assertNotNull(request);
        assertEquals(request.key, BigInteger.valueOf(5).toString(16));
        assertEquals(request.value, "value");
    }

    @Test
    public void testReceive() {
        Map<String,String> storage = new HashMap<>();
        FakeConnection conn = new FakeConnection();
        String key = BigInteger.valueOf(5).toString(16);
        new PutProtocol(conn, storage)
            .receive(GSON.toJson(new PutRequest(key, "value")));
        assertTrue(storage.size() == 1);
        assertTrue(storage.containsKey(key));
        assertEquals(storage.get(key), "value");
    }
}
