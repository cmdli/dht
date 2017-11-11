
package com.cmdli.dht.messages;

import java.util.List;

import com.cmdli.dht.Node;
import com.cmdli.dht.messages.Message;

public class FindNodeResponse extends Message {
    public List<Node> nodes;
    public FindNodeResponse(List<Node> nodes) {
        super("FindNodeResponse");
        this.nodes = nodes;
    }
}
