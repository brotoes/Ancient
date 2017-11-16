/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.network.messages;

import actionTracker.ActionTracker;
import network.Server;
import network.Client;
import network.messages.Message;

/**
 * shares a list of all actions performed
 * @author brock
 */
public class ActionsMessage extends Message {
    ActionTracker at;

    public ActionsMessage(ActionTracker at) {
        this.at = at;
    }

    /**
     * No-arg constructor for Kryo
     */
    private ActionsMessage() {}

    @Override
    public void receive(Server server) {
        while(at.reproduce());
    }

    @Override
    public void send(Client client) {
        client.send(this);
    }
}
