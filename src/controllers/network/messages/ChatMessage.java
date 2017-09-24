/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import ancient.Main;
import controllers.gui.ParentScreenController;
import network.Client;
import network.Connection;
import network.messages.Message;
import network.Server;
import network.exceptions.MalformedMessageException;

/**
 *
 * @author brock
 */
public class ChatMessage extends Message {
    public final static String ID = "CHAT";

    private final ParentScreenController con;
    private final String line;

    public ChatMessage(String line) {
        con = (ParentScreenController)Main.app.getNifty().getCurrentScreen().getScreenController();
        this.line = line;
    }

    public static ChatMessage parse(Connection conn, String msg) throws MalformedMessageException {
        String[] split = msg.split(" ", 2);
        if (!split[0].equals(ID)) {
            throw new MalformedMessageException();
        }

        ChatMessage parsed = new ChatMessage(split[1]);
        parsed.setConnection(conn);

        return parsed;
    }

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

    @Override
    public String toString() {
        String str = getId() + " " + line;

        return str;
    }
}
