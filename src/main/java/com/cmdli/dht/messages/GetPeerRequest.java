package com.cmdli.dht.messages;

import com.cmdli.dht.protocols.GetPeerProtocol;

public class GetPeerRequest extends Message {
    public GetPeerRequest() {
        super("GetPeerRequest", GetPeerProtocol.NAME);
    }
}
