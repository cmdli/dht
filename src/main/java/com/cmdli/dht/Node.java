
package com.cmdli.dht;

import java.math.BigInteger;
import java.net.InetAddress;

public class Node {
    private BigInteger id;
    private InetAddress address;
    private int port;

    public Node(BigInteger id, InetAddress address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public BigInteger id() {
        return id;
    }

    public InetAddress address() {
        return address;
    }

    public int port() {
        return port;
    }

    public String toString() {
        return "0x" + id.toString(16) + " " + address + ":" + port;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Node))
            return false;
        Node otherNode = (Node)other;
        if (!otherNode.id.equals(this.id))
            return false;
        if (!otherNode.address.equals(this.address))
            return false;
        if (otherNode.port != this.port)
            return false;
        return true;
    }
}
