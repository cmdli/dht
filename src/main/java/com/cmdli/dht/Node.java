
package com.cmdli.dht;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Objects;

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

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!(other instanceof Node))
            return false;
        Node otherNode = (Node)other;
        if (!Objects.equals(this.id, otherNode.id))
            return false;
        if (!Objects.equals(this.address, otherNode.address))
            return false;
        if (otherNode.port != this.port)
            return false;
        return true;
    }
}
