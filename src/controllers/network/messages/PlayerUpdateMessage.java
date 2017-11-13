/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import ancient.Main;
import ancient.players.Player;
import java.io.IOException;
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
    /* indicates this is the client's player */
    private final Boolean local;

    /**
     * assembles the existing list of players from PlayerManager
     * @param player
     * @param local
     */
    public PlayerUpdateMessage(Player player, Boolean local) {
        this.player = player;
        this.local = local;
    }
    public PlayerUpdateMessage(Player player) {
        this(player, false);
    }

    public static PlayerUpdateMessage parse(Connection conn, String msg) throws MalformedMessageException {
        String[] split = msg.split(" ", 3);
        if (!split[0].equals(ID)) {
            throw new MalformedMessageException();
        }

        try {
            Player player = (Player) StrUtils.fromString(split[1], Player.class);
            Boolean local = (Boolean) StrUtils.fromString(split[2], Boolean.class);
            PlayerUpdateMessage parsed = new PlayerUpdateMessage(player, local);
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
        if (Main.app.getPlayerManager().addPlayer(player)) {
            player.setLocal(local);
        }
        Main.app.getState().updatePlayer(player);
        System.out.println(player + " " + player.isLocal());
    }

    @Override
    public String toString() {
        String str = getId();

        try {
            str += " " + StrUtils.toString(player) + " " + StrUtils.toString(local);

            return str;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
