/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import network.messages.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import network.messages.QuitMessage;

/**
 * Accepts and manages connections
 * @author brock
 */
public class Server {
    private final ListenerThread listener;
    private final List<Connection> connections;
    private ServerSocket serverSocket;

    protected Server(MessageManager msgMgr) throws IOException {
        connections = new CopyOnWriteArrayList<>();

        try {
            serverSocket = new ServerSocket(msgMgr.getPort());
        } catch (IOException e) {
            listener = null;
            throw e;
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
     * Sends a message to its destination
     * @param msg
     */
    public void send(Message msg) {
        if (msg.getConnection() != null) {
            msg.getConnection().send(msg.toString());
        } else {
            connections.stream().forEach(c -> c.send(msg.toString()));
        }
    }

    /**
     * closes all connections
     */
    public void close() {
        try {
            QuitMessage qMsg = new QuitMessage();
            send(qMsg);
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void close(Connection c) {
        connections.remove(c);
    }

    public static void main(String args[]) {
        MessageManager msgMgr = new MessageManager(1234);
        try {
            msgMgr.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            msgMgr.receive();
        }
    }

    /* getters and setters */
    protected int getConnectionCount() {
        return connections.size();
    }
}
