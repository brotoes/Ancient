/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network;

import controllers.network.messages.JoinMessage;
import controllers.network.messages.PlayerListMessage;
import exceptions.CreateException;
import exceptions.JoinException;
import java.io.IOException;
import java.net.ConnectException;
import network.messages.EchoMessage;
import network.MessageManager;

/**
 *
 * @author brock
 */
public class NetworkController {
    private final int PORT = 8124;

    private MessageManager hostMgr;
    private MessageManager clientMgr;

    /**
     * hosts a game and joins own game
     * @throws exceptions.CreateException
     * @throws exceptions.JoinException
     */
    public void host() throws CreateException, JoinException {
        hostMgr = new MessageManager(PORT);
        register(hostMgr);
        try {
            hostMgr.listen();
        } catch (IOException e) {
            throw new CreateException(e);
        }

        clientMgr = new MessageManager(PORT);
        register(clientMgr);
        try {
            clientMgr.connect("127.0.0.1");
        } catch (ConnectException e) {
            throw new JoinException(e);
        }

        join();
    }

    /**
     * connects to a hosted game
     * @param address
     * @throws exceptions.JoinException
     */
    public void connect(String address) throws JoinException {
        hostMgr = null;

        clientMgr = new MessageManager(PORT);
        register(clientMgr);
        try {
            clientMgr.connect(address);
        } catch (ConnectException e) {
            throw new JoinException(e);
        }
        join();
    }

    /**
     * Joins connected game
     */
    private void join() {
        JoinMessage joinMsg = new JoinMessage("Phteven");
        clientMgr.send(joinMsg);
    }

    /**
     * registers all required messages for m
     * @param m
     */
    private void register(MessageManager m) {
        m.register(JoinMessage.class);
        m.register(PlayerListMessage.class);
    }

    public void update() {
        if (clientMgr != null) {
            clientMgr.receive();
        }
        if (hostMgr != null) {
            hostMgr.receive();
        }
    }

    /**
     * closes all connections
     */
    public void close() {
        if (clientMgr != null) {
            clientMgr.close();
        }
        if (hostMgr != null) {
            hostMgr.close();
        }
    }

    /**
     * sends a message to the server to be echoed. Confirms that everything works
     * @param line
     */
    public void echo(String line) {
        EchoMessage msg = new EchoMessage(line);
        clientMgr.send(msg);
    }
}
