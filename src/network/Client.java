/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import de.lessvoid.nifty.elements.render.TextRenderer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * connects to a server
 * @author brock
 */
public class Client {
    private final int PORT = 8413;

    private InetAddress address;
    private Socket sock = null;
    private ClientConnectionThread connection;

    public Client(String hostname, TextRenderer output) {
        try {
            address = InetAddress.getByName(hostname.trim());
            sock = new Socket(address, PORT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        connection = new ClientConnectionThread(sock, output);
    }

    public void start() {
        connection.start();
    }

    /**
     * sends message to the server
     * @param line
     */
    public void send(String line) {
        connection.send("line");
    }
}
