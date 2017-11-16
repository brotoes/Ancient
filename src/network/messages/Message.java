/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.messages;

import java.io.IOException;
import network.Client;
import network.Connection;
import network.Server;
import utils.StrUtils;

/**
 *
 * @author brock
 */
public abstract class Message {
    /**
     * No-arg constructor for Kryo
     */
    protected Message() {}

    /**
     * key for socket of sender. Should only be set serverside
     */
    private Connection connection = null;

    public void send(Server server) {
        throw new UnsupportedOperationException(this.getClass().getName() + " Not to be sent from server.");
    }

    public void receive(Server server) {
        throw new UnsupportedOperationException(this.getClass().getName() + " Not to be received on server.");
    }

    public void send(Client client) {
        throw new UnsupportedOperationException(this.getClass().getName() + " Not to be sent from client.");
    }

    public void receive(Client client) {
        throw new UnsupportedOperationException(this.getClass().getName() + " Not to be received on server.");
    }

    /* getters and setters */
    public final void setConnection(Connection connection) { this.connection = connection; }
    public final Connection getConnection() { return connection; }
    @Override
    public String toString() {
        try {
            return StrUtils.toString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
