/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import ancient.Main;
import ancient.map.WorldMap;
import network.Server;
import network.Client;
import network.messages.Message;

/**
 *
 * @author brock
 */
public class StartGameMessage extends Message {
    private WorldMap wm;

    public StartGameMessage(WorldMap wm) {
        this.wm = wm;
    }

    /**
     * No-arg constructor for Kryo
     */
    private StartGameMessage() {}

    @Override
    public void send(Server server) {
        server.send(this);
    }

    @Override
    public void receive(Client client) {
        Main.app.gotoGame(wm);
    }
}
