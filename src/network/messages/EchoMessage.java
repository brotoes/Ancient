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
public class EchoMessage extends Message {
    public final static String ID = "ECHO";

    private final String body;

    /**
     * parses a string representing an EchoMessage sent from key
     * @param conn
     * @param msg
     * @return
     * @throws MalformedMessageException
     */
    public static EchoMessage parse(Connection conn, String msg) throws MalformedMessageException {
        String[] split = msg.split(" ", 2);
        if (!split[0].equals(ID)) {
            throw new MalformedMessageException();
        }

        EchoMessage parsed = new EchoMessage(split[1]);
        parsed.setConnection(conn);

        return parsed;
    }

    /**
     * takes a key and body for sending a message
     * @param body
     */
    public EchoMessage(String body) {
        this.body = body;
    }

    @Override
    public void receive(Server server) {
        System.out.println("received");
        server.send(this);
    }

    @Override
    public void send(Client client) {
        client.send(this);
    }

    @Override
    public void receive(Client client) {
        System.out.println(body);
    }

    @Override
    public String toString() {
        return getId() + " " + body;
    }
}
