
package com.cmdli.dht.messages;

import com.cmdli.dht.messages.Message;
import com.cmdli.dht.protocols.PutProtocol;

public class PutRequest extends Message {
    public String key;
    public String value;
    public PutRequest(String key, String value) {
        super("PutRequest", PutProtocol.NAME);
        this.key = key;
        this.value = value;
    }
}
