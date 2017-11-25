
package com.cmdli.dht.messages;

import java.util.List;
import java.util.Objects;

import com.cmdli.dht.Node;
import com.cmdli.dht.messages.Message;

public class FindNodeResponse extends Message {
    public List<Node> nodes;
    public FindNodeResponse(List<Node> nodes) {
        super("FindNodeResponse");
        this.nodes = nodes;
    }

    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || !(other instanceof FindNodeResponse))
            return false;
        FindNodeResponse otherResp = (FindNodeResponse)other;
        if (!Objects.equals(otherResp.nodes, this.nodes))
            return false;
        return true;
    }
}
