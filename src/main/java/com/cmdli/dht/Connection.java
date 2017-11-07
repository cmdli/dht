
package com.cmdli.dht;

import java.io.*;
import java.net.*;

import com.cmdli.dht.Node;

public class Connection implements Closeable {

    public final static boolean LOGGING = false;
    
    private Socket socket;

    public Connection() {
        this(null);
    }
    
    public Connection(Socket socket) {
        this.socket = socket;
        //        if (socket != null) {
        //            this.socket.setTcpNoDelay(true);
        //        }
    }

    public Connection connect(Node node) {
        try {
            if (LOGGING) System.out.printf("Connecting to %s:%d... ", node.address(), node.port());
            socket = new Socket(node.address(),node.port());
            if (LOGGING) System.out.println("Connected.");
        } catch (IOException e) {
            System.err.println(e);
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
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            if (LOGGING) System.out.println("Sending - " + message);
            out.write(message);
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
