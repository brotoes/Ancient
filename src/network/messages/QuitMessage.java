/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.messages;

import network.Client;
import network.Connection;
import network.Server;
import network.exceptions.MalformedMessageException;

/**
 *
 * @author brock
 */
public class QuitMessage extends Message {
    public final static String ID = "QUIT";

    /**
     * parses a string representing an EchoMessage sent from key
     * @param conn
     * @param msg
     * @return
     * @throws MalformedMessageException
     */
    public static QuitMessage parse(Connection conn, String msg) throws MalformedMessageException {
        msg = msg.trim();
        if (!msg.equals(ID)) {
            throw new MalformedMessageException();
        }

        QuitMessage parsed = new QuitMessage();
        parsed.setConnection(conn);

        return parsed;
    }

    @Override
    public void send(Server server) {
        server.send(this);
    }

    @Override
    public void receive(Server server) {
        server.close(getConnection());
    }

    @Override
    public void send(Client client) {
        client.send(this);
    }

    @Override
    public void receive(Client client) {
        System.out.println("Client got close message");
    }

    @Override
    public String toString() {
        return getId();
    }
}
