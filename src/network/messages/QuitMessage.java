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
public class QuitMessage extends Message {
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
}
