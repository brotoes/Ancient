/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import ancient.Main;
import ancient.players.Player;
import java.io.IOException;
import java.io.Serializable;
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
public class PlayerUpdateMessage extends Message {
    public final static String ID = "PLAYERUPDATE";

    private final Player player;

    /**
     * assembles the existing list of players from PlayerManager
     * @param player
     */
    public PlayerUpdateMessage(Player player) {
        this.player = player;
    }

    public static PlayerUpdateMessage parse(Connection conn, String msg) throws MalformedMessageException {
        String[] split = msg.split(" ", 2);
        if (!split[0].equals(ID)) {
            throw new MalformedMessageException();
        }

        try {
            Player player = (Player) StrUtils.fromString(split[1]);
            PlayerUpdateMessage parsed = new PlayerUpdateMessage(player);
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
    public void receive(Server server) {
        Main.app.getPlayerManager().getPlayer(player.getId()).update(player);
        Main.app.getState().updatePlayer(player);
        server.send(this);
    }

    @Override
    public void send(Client client) {
        client.send(this);
    }

    @Override
    public void receive(Client client) {
        Main.app.getPlayerManager().addPlayer(player);
        Main.app.getState().updatePlayer(player);
    }

    @Override
    public String toString() {
        String str = getId();

        try {
            str += " " + StrUtils.toString((Serializable)player);

            return str;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
