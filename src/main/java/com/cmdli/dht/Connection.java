
package com.cmdli.dht;

import java.io.*;
import java.net.*;

import com.cmdli.dht.Node;

public class Connection implements Closeable {

    public final static boolean LOGGING = false;

    private Node remoteNode;
    private Socket socket;

    public Connection() {
        this(null);
    }
    
    public Connection(Socket socket) {
        this.socket = socket;
    }

    public Connection connect(Node node) {
        try {
            this.remoteNode = node;
            if (LOGGING) System.out.printf("Connecting to %s:%d... ", node.address(), node.port());
            socket = new Socket(node.address(),node.port());
            if (LOGGING) System.out.println("Connected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean connected() {
        return socket != null;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            socket = null;
        }
    }
    
    public InetAddress address() {
        return socket != null ? socket.getInetAddress() : null;
    }

    public int port() {
        return socket != null ? socket.getLocalPort() : -1;
    }
    
    public void send(String message) {
        if (socket == null)
            return;
        if (message == null)
            throw new IllegalArgumentException("Trying to send null message");
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            if (LOGGING) {
                if (this.remoteNode != null)
                    System.out.println("Sending to " + remoteNode.id() + " - " + message);
                else
                    System.out.println("Responding - " + message);
            }
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public String receive() {
        if (socket == null)
            return null;
        String message = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            message = in.readLine();
            if (LOGGING) System.out.println("Received - " + message);
        } catch (IOException e) {
            System.err.println(e);
        }
        return message;
    }
    
}
