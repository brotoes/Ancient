/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import actionTracker.ActionTracker;
import ancient.Main;
import ancient.map.WorldMap;
import java.io.IOException;
import network.Connection;
import network.Server;
import network.Client;
import network.exceptions.MalformedMessageException;
import network.messages.Message;
import utils.StrUtils;

/**
 * shares a list of all actions performed
 * @author brock
 */
public class ActionsMessage extends Message {
    public final static String ID = "ACTIONS";

    ActionTracker at;

    public ActionsMessage(ActionTracker at) {
        this.at = at;
    }

    public static ActionsMessage parse(Connection conn, String msg) throws MalformedMessageException {
        String[] split = msg.split(" ", 2);
        if (!split[0].equals(ID)) {
            throw new MalformedMessageException();
        }

        try {
            ActionTracker at = (ActionTracker)StrUtils.fromString(split[1], ActionTracker.class);

            ActionsMessage parsed = new ActionsMessage(at);
            parsed.setConnection(conn);

            return parsed;
        } catch (IOException | ClassNotFoundException ex) {
            throw new MalformedMessageException(ex);
        }
    }

    @Override
    public void receive(Server server) {
        while(at.reproduce());
    }

    @Override
    public void send(Client client) {
        client.send(this);
    }

    @Override
    public String toString() {
        try {
            return getId() + " " + StrUtils.toString(at);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
