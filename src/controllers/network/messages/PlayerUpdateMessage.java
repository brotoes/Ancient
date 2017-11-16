/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import ancient.Main;
import ancient.players.Player;
import network.Client;
import network.messages.Message;
import network.Server;

/**
 * Sent from the server to clients to update the list of players
 * @author brock
 */
public class PlayerUpdateMessage extends Message {
    private Player player;
    /* indicates this is the client's player */
    private Boolean local;

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

    /**
     * No-arg constructor for Kryo
     */
    private PlayerUpdateMessage() {}

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
}
