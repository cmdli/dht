
package com.cmdli.dht.messages;

import java.util.List;

import com.cmdli.dht.Node;
import com.cmdli.dht.messages.Message;

public class GetResponse extends Message {
    public List<Node> nodes;
    public GetResponse(List<Node> nodes) {
        super("GetResponse");
        this.nodes = nodes;
    }
}
