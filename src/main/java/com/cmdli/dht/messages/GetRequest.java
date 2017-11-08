
package com.cmdli.dht.messages;

import java.math.BigInteger;

import com.cmdli.dht.messages.Message;

public class GetRequest extends Message {
    public BigInteger key;
    
    public GetRequest(BigInteger key) {
        super("GetRequest");
        this.key = key;
    }
}
