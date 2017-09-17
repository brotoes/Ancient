/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * Listens for new connections
 * @author brock
 */
class ListenerThread<T> extends Thread {
    private final ServerSocket serverSocket;
    private final List<Connection> connections;
    private final MessageManager messageManager;

    protected ListenerThread(ServerSocket serverSocket,
            List<Connection> connections, MessageManager msgMgr) {
        this.serverSocket = serverSocket;
        this.connections = connections;
        this.messageManager = msgMgr;
    }

    /**
     * creates new thread to listen for connections
     */
    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Connection conn = new Connection(socket, messageManager);
                conn.start();
                connections.add(conn);
            } catch (SocketException e) {
                break;
            } catch (IOException e) {
                break;
            }
        }
    }
}
