
package com.cmdli.dht;

import java.util.BitSet;
import java.net.InetAddress;

public class Node {
    private BitSet id;
    private InetAddress address;
    private int port;

    public Node(BitSet id, InetAddress address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public BitSet id() {
        return id;
    }

    public InetAddress address() {
        return address;
    }

    public int port() {
        return port;
    }

    public String toString() {
        return String.format("0x%x", id.hashCode()) + " " + address;
    }
}
