/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Accepts and manages connections
 * @author brock
 */
public class Server {
    private final int PORT = 8413;
    private final ListenerThread listener;

    public Server() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
            listener = null;
            return;
        }

        listener = new ListenerThread(serverSocket);
    }

    /**
     * creates new thread to listen for connections
     */
    public void listen() {
        listener.start();
    }
}
