/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import ancient.Main;
import controllers.gui.ParentScreenController;
import network.Client;
import network.messages.Message;
import network.Server;

/**
 *
 * @author brock
 */
public class ChatMessage extends Message {
    private ParentScreenController con;
    private String line;

    public ChatMessage(String line) {
        con = (ParentScreenController)Main.app.getNifty().getCurrentScreen().getScreenController();
        this.line = line;
    }

    /**
     * No-arg constructor for Kryo
     */
    private ChatMessage() {}

    @Override
    public void receive(Server server) {
        //broadcast chat
        con.receiveChat(line);
        server.send(this);
    }

    @Override
    public void send(Server server) {
        con.receiveChat(line);
        server.send(this);
    }

    @Override
    public void send(Client client) {
        client.send(this);
    }

    @Override
    public void receive(Client client) {
        con.receiveChat(line);
    }
}
