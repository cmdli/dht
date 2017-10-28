
package com.cmdli.dht;

import java.io.*;
import java.net.*;

public class TestServer {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java TestServer <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader networkIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = null;
                while ((inputLine = networkIn.readLine()) != null) {
                    System.out.println(inputLine);
                }
                networkIn.close();
                clientSocket.close();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
}
