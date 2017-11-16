/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.messages;

import network.Client;
import network.Server;

/**
 *
 * @author brock
 */
public class EchoMessage extends Message {
    private String body;

    /**
     * takes a key and body for sending a message
     * @param body
     */
    public EchoMessage(String body) {
        this.body = body;
    }

    /**
     * No-arg constructor for Kryo
     */
    private EchoMessage() {}

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
}
