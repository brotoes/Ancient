/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.messages;

import java.lang.reflect.Field;
import network.Client;
import network.Connection;
import network.Server;

/**
 *
 * @author brock
 */
public abstract class Message {
    /**
     * key for socket of sender. Should only be set serverside
     */
    private Connection connection = null;

    public void send(Server server) {
        throw new UnsupportedOperationException(getId() + " Not to be sent from server.");
    }

    public void receive(Server server) {
        throw new UnsupportedOperationException(getId() + " Not to be received on server.");
    }

    public void send(Client client) {
        throw new UnsupportedOperationException(getId() + " Not to be sent from client.");
    }

    public void receive(Client client) {
        throw new UnsupportedOperationException(getId() + " Not to be received on server.");
    }

    /* getters and setters */
    public final void setConnection(Connection connection) { this.connection = connection; }
    public final Connection getConnection() { return connection; }

    public final String getId() {
        try {
            Field field = this.getClass().getDeclaredField("ID");
            return (String)field.get(null);
        } catch (IllegalArgumentException | IllegalAccessException| NoSuchFieldException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
