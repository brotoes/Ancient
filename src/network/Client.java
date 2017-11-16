/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import network.messages.EchoMessage;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import network.messages.Message;

/**
 * connects to a server
 * @author brock
 */
public class Client {
    private InetAddress address;
    private Socket sock = null;
    private final Connection connection;
    private final MessageManager messageManager;

    protected Client(MessageManager msgMgr, String hostname) throws ConnectException {
        this.messageManager = msgMgr;
        try {
            address = InetAddress.getByName(hostname.trim());
            sock = new Socket(address, msgMgr.getPort());
        } catch (ConnectException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            connection = null;
            return;
        }

        connection = new Connection(sock, messageManager);
    }

    protected void start() {
        if (connection != null) {
            connection.start();
        }
    }

    /**
     * sends message to the server
     * @param msg
     */
    public void send(Message msg) {
        connection.send(msg.toString());
    }

    public static void main(String args[]) {
        MessageManager msgMgr = new MessageManager(1234);
        try {
            msgMgr.connect("127.0.0.1");
        } catch (ConnectException e) {
            System.out.println("No Server Listening");
            return;
        }

        Scanner s = new Scanner(System.in);
        System.out.print("Send: ");
        String line = s.nextLine();
        EchoMessage msg = new EchoMessage(line);
        msgMgr.send(msg);
        while (!msgMgr.receive()) {}
    }
}
