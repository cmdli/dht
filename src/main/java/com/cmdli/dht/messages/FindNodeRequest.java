
package com.cmdli.dht.messages;

import java.math.BigInteger;

import com.cmdli.dht.messages.Message;

public class FindNodeRequest extends Message {
    public BigInteger key;
    public FindNodeRequest(BigInteger key) {
        super("FindNodeRequest");
        this.key = key;
    }
}
