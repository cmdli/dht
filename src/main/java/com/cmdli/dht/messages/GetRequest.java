
package com.cmdli.dht.messages;

import java.math.BigInteger;

import com.cmdli.dht.messages.Message;
import com.cmdli.dht.protocols.GetProtocol;

public class GetRequest extends Message {
    public BigInteger key;
    
    public GetRequest(BigInteger key) {
        super("GetRequest", GetProtocol.NAME);
        this.key = key;
    }
}
