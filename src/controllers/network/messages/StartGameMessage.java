/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

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
 *
 * @author brock
 */
public class StartGameMessage extends Message {
    public final static String ID = "STARTGAME";

    private String wmStr;
    private WorldMap wm;

    public StartGameMessage(WorldMap wm) {
        try {
            this.wmStr = StrUtils.toString(wm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static StartGameMessage parse(Connection conn, String msg) throws MalformedMessageException {
        String[] split = msg.split(" ", 2);
        if (!split[0].equals(ID)) {
            throw new MalformedMessageException();
        }

        try {
            WorldMap wm = (WorldMap)StrUtils.fromString(split[1]);
            StartGameMessage parsed = new StartGameMessage(wm);
            parsed.setConnection(conn);

            return parsed;
        } catch (IOException | ClassNotFoundException ex) {
            throw new MalformedMessageException(ex);
        }
    }

    @Override
    public void send(Server server) {
        server.send(this);
    }

    @Override
    public void receive(Client client) {
        Main.app.gotoGame(wm);
    }

    @Override
    public String toString() {
        return getId() + " " + wmStr;
    }
}
