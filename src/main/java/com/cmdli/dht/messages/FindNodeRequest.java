
package com.cmdli.dht.messages;

import java.util.Objects;
import java.math.BigInteger;

import com.cmdli.dht.messages.Message;

public class FindNodeRequest extends Message {
    public BigInteger key;
    public FindNodeRequest(BigInteger key) {
        super("FindNodeRequest");
        this.key = key;
    }

    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null && !(other instanceof FindNodeRequest))
            return false;
        FindNodeRequest otherReq = (FindNodeRequest)other;
        if (!Objects.equals(otherReq.key,this.key))
            return false;
        return true;
    }
}
