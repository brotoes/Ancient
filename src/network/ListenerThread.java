/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens for new connections
 * @author brock
 */
class ListenerThread extends Thread {
    private final int PORT = 8413;
    private final ServerSocket serverSocket;

    protected ListenerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * creates new thread to listen for connections
     */
    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Connected!");
                new ServerConnectionThread(socket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
