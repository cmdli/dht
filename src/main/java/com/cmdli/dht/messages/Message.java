
package com.cmdli.dht.messages;

public class Message {
    public String type;
    public String protocol;
    public Message(String type, String protocol) {
        this.type = type;
        this.protocol = protocol;
    }
}
