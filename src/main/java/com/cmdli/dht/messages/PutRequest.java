
package com.cmdli.dht.messages;

import com.cmdli.dht.messages.Message;

public class PutRequest extends Message {
    public String key;
    public String value;
    public PutRequest(String key, String value) {
        super("PutRequest");
        this.key = key;
        this.value = value;
    }
}
