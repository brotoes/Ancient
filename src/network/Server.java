/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Accepts and manages connections
 * @author brock
 */
public class Server {
    private final int PORT = 8413;
    private final ListenerThread listener;
    private final List<Connection> connections;

    protected Server(MessageManager msgMgr) {
        ServerSocket serverSocket;
        connections = new CopyOnWriteArrayList<>();

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
            listener = null;
            return;
        }

        listener = new ListenerThread(serverSocket, connections, msgMgr);
    }

    /**
     * creates new thread to listen for connections
     */
    protected void listen() {
        listener.start();
    }

    /**
     * sends message to all clients
     * @param index
     * @param line
     */
    protected void send(int index, String line) {
        connections.get(index).send(line);
    }

    public static void main(String args[]) {
        MessageManager msgMgr = new MessageManager();
        msgMgr.register(EchoMessage.class);
        msgMgr.listen();

        while (true) {
            msgMgr.receive();
        }
    }
}
