package com.cmdli.dht.messages;

import com.cmdli.dht.Node;
import com.cmdli.dht.protocols.GetPeerProtocol;

import java.util.List;

public class GetPeerResponse extends Message {
    public List<Node> nodes;
    public GetPeerResponse(List<Node> nodes) {
        super("GetPeerResponse", GetPeerProtocol.NAME);
        this.nodes = nodes;
    }
}
