
package com.cmdli.dht.messages;

import java.util.List;

import com.cmdli.dht.Node;
import com.cmdli.dht.messages.Message;
import com.cmdli.dht.protocols.GetProtocol;

public class GetResponse extends Message {
    public List<Node> nodes;
    public String value;
    public GetResponse(List<Node> nodes, String value) {
        super("GetResponse", GetProtocol.NAME);
        this.nodes = nodes;
        this.value = value;
    }
}
