
package com.cmdli.dht;

import java.util.*;

import com.cmdli.dht.*;

public class FakeConnection extends Connection {

    private List<String> sentMessages;
    private LinkedList<String> receivedMessages;

    public FakeConnection() {
        this.sentMessages = new ArrayList<String>();
        this.receivedMessages = new LinkedList<String>();
    }
    
    public void send(String message) {
        sentMessages.add(message);
    }

    public List<String> getSentMessages() {
        List<String> messages = sentMessages;
        sentMessages = new ArrayList<String>();
        return messages;
    }

    public String receive() {
        return receivedMessages.poll();
    }

    public void addReceivedMessage(String message) {
        receivedMessages.push(message);
    }
}
