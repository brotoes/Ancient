/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network;

import ancient.Main;
import ancient.players.Player;
import controllers.network.messages.ChatMessage;
import controllers.network.messages.JoinMessage;
import controllers.network.messages.PlayerUpdateMessage;
import controllers.network.messages.StartGameMessage;
import exceptions.CreateException;
import exceptions.JoinException;
import java.io.IOException;
import java.net.ConnectException;
import network.messages.EchoMessage;
import network.MessageManager;
import network.messages.Message;

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
     * @param name
     * @throws exceptions.CreateException
     * @throws exceptions.JoinException
     */
    public void host(String name) throws CreateException, JoinException {
        hostMgr = new MessageManager(PORT);
        register(hostMgr);
        try {
            hostMgr.listen();
        } catch (IOException e) {
            throw new CreateException(e);
        }

        Main.app.getPlayerManager().addPlayer(new Player(name));
    }

    /**
     * connects to a hosted game
     * @param address
     * @param name
     * @throws exceptions.JoinException
     */
    public void connect(String address, String name) throws JoinException {
        hostMgr = null;

        clientMgr = new MessageManager(PORT);
        register(clientMgr);
        try {
            clientMgr.connect(address);
        } catch (ConnectException e) {
            throw new JoinException(e);
        }
        JoinMessage joinMsg = new JoinMessage(name);
        clientMgr.send(joinMsg);
    }

    /**
     * registers all required messages for m
     * @param m
     */
    private void register(MessageManager m) {
        m.register(JoinMessage.class);
        m.register(PlayerUpdateMessage.class);
        m.register(ChatMessage.class);
        m.register(StartGameMessage.class);
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

    public void send(Message msg) {
        if (clientMgr != null) {
            clientMgr.send(msg);
        }
        if (hostMgr != null) {
            hostMgr.send(msg);
        }
    }

    public boolean isHost() { return hostMgr != null; }
}
