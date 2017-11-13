/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network;

import ancient.players.Player;
import controllers.network.messages.ActionsMessage;
import controllers.network.messages.ChatMessage;
import controllers.network.messages.JoinMessage;
import controllers.network.messages.PlayerUpdateMessage;
import controllers.network.messages.StartGameMessage;
import exceptions.CreateException;
import exceptions.JoinException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import network.Connection;
import network.messages.EchoMessage;
import network.MessageManager;
import network.messages.Message;

/**
 *
 * @author brock
 */
public class NetworkController {
    private final int PORT = 8124;
    private final Map<Player, Connection> connMap = new HashMap<>();
    private MessageManager mm;

    /**
     * hosts a game and joins own game
     * @param name
     * @throws exceptions.CreateException
     * @throws exceptions.JoinException
     */
    public void host(String name) throws CreateException, JoinException {
        mm = new MessageManager(PORT);
        register();
        try {
            mm.listen();
        } catch (IOException e) {
            throw new CreateException(e);
        }
    }

    /**
     * connects to a hosted game
     * @param address
     * @param name
     * @throws exceptions.JoinException
     */
    public void connect(String address, String name) throws JoinException {
        mm = new MessageManager(PORT);
        register();
        try {
            mm.connect(address);
        } catch (ConnectException e) {
            throw new JoinException(e);
        }
        JoinMessage joinMsg = new JoinMessage(name);
        mm.send(joinMsg);
    }

    /**
     * registers all required messages for m
     * @param m
     */
    private void register() {
        mm.register(JoinMessage.class);
        mm.register(PlayerUpdateMessage.class);
        mm.register(ChatMessage.class);
        mm.register(StartGameMessage.class);
        mm.register(ActionsMessage.class);
    }

    public void update() {
        if (mm != null) {
            mm.receive();
        }
    }

    /**
     * closes all connections
     */
    public void close() {
        if (mm != null) {
            mm.close();
        }
    }

    /**
     * sends a message to the server to be echoed. Confirms that everything works
     * @param line
     */
    public void echo(String line) {
        EchoMessage msg = new EchoMessage(line);
        mm.send(msg);
    }

    public void send(Message msg) {
        if (mm != null) {
            mm.send(msg);
        }
    }

    public void send(Message msg, Player player) {
        Connection conn = getConnection(player);
        msg.setConnection(conn);
        send(msg);
    }

    public boolean isHost() { return mm.isServer(); }
    public void setPlayerConnection(Player player, Connection conn) {
        connMap.put(player, conn);
    }
    public Connection getConnection(Player player) {
        return connMap.get(player);
    }
    public Map<Player, Connection> getConnectionMap() {
        return Collections.unmodifiableMap(connMap);
    }
}
