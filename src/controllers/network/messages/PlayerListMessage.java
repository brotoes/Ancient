/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import ancient.Main;
import ancient.players.Player;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import network.Client;
import network.Connection;
import network.messages.Message;
import network.Server;
import network.exceptions.MalformedMessageException;
import utils.StrUtils;

/**
 * Sent from the server to clients to update the list of players
 * @author brock
 */
public class PlayerListMessage extends Message {
    public final static String ID = "PLAYERLIST";

    private final List<Player> players;

    /**
     * assembles the existing list of players from PlayerManager
     */
    public PlayerListMessage() {
        players = Main.app.getPlayerManager().getPlayers();
    }

    /**
     * constructs with preset list of players
     * @param players
     */
    public PlayerListMessage(List<Player> players) {
        this.players = players;
    }

    public static PlayerListMessage parse(Connection conn, String msg) throws MalformedMessageException {
        String[] split = msg.split(" ", 2);
        if (!split[0].equals(ID)) {
            throw new MalformedMessageException();
        }

        try {
            List<Player> players = (List<Player>) StrUtils.fromString(split[1]);
            PlayerListMessage parsed = new PlayerListMessage(players);
            parsed.setConnection(conn);

            return parsed;
        } catch (IOException | ClassNotFoundException e) {
            throw new MalformedMessageException(e);
        }
    }

    @Override
    public void send(Server server) {
        server.send(this);
    }

    @Override
    public void receive(Client client) {
        players.stream().forEach(p -> Main.app.getPlayerManager().addPlayer(p));
    }

    @Override
    public String toString() {
        String str = getId();

        try {
            str += " " + StrUtils.toString((Serializable)players);

            return str;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
