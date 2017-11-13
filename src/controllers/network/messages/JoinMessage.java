/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import ancient.Main;
import ancient.players.Player;
import java.util.List;
import network.Client;
import network.Connection;
import network.messages.Message;
import network.Server;
import network.exceptions.MalformedMessageException;

/**
 *
 * @author brock
 */
public class JoinMessage extends Message {
    public final static String ID = "JOIN";

    private final String name;

    public JoinMessage(String name) {
        this.name = name;
    }

    public static JoinMessage parse(Connection conn, String msg) throws MalformedMessageException {
        String[] split = msg.split(" ", 2);
        if (!split[0].equals(ID)) {
            throw new MalformedMessageException();
        }

        JoinMessage parsed = new JoinMessage(split[1]);
        parsed.setConnection(conn);

        return parsed;
    }

    @Override
    public void receive(Server server) {
        Player player = Main.app.getPlayerManager().addPlayer(name);
        List<Player> players = Main.app.getPlayerManager().getPlayers();

        //broadcast playerupdatemessage for new player
        Message msg = new PlayerUpdateMessage(player, true);
        server.send(msg);
        Main.app.getNetworkController().setPlayerConnection(player, getConnection());

        for (int i = players.size() - 2; i >= 0; i --) {
            //send playerupdatemessage for each other player to new connection
            msg = new PlayerUpdateMessage(players.get(i), false);
            msg.setConnection(getConnection());
            server.send(msg);
        }
    }

    @Override
    public void send(Client client) {
        client.send(this);
    }

    @Override
    public String toString() {
        String str = getId() + " " + name;

        return str;
    }
}
