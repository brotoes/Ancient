/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import network.messages.QuitMessage;
import network.messages.Message;
import network.messages.EchoMessage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import network.exceptions.MalformedMessageException;

/**
 *
 * @author brock
 */
public class MessageManager {
    private final int PORT;
    private final Map<String, Class<? extends Message>> msgs = new HashMap<>();
    private final BlockingQueue<Message> receiveQueue = new LinkedBlockingQueue<>();
    private Server server = null;
    private Client client = null;

    public MessageManager(int port) {
        this.PORT = port;

        register(EchoMessage.class);
        register(QuitMessage.class);
    }

    /**
     * takes a Message and adds its class to the map of messages.
     * The message itself it not used an may be discarded.
     * @param cls
     */
    public final void register(Class<? extends Message> cls) {
        try {
            msgs.put((String)cls.getField("ID").get(null), cls);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Takes string, creates and executes the corresponding message on server
     * @param conn
     * @param msg
     * @throws network.exceptions.MalformedMessageException
     */
    public void parse(Connection conn, String msg) throws MalformedMessageException {
        msg = msg.trim();
        String id = msg.split(" ", 2)[0];
        Class<? extends Message> cls = msgs.get(id);

        if (cls == null) {
            throw new MalformedMessageException("Invalid Message ID: " + id);
        }
        try {
            Message parsed = (Message)cls
                    .getDeclaredMethod("parse", Connection.class, String.class)
                    .invoke(null, conn, msg);
            receiveQueue.put(parsed);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            throw new MalformedMessageException(e);
        } catch (InterruptedException ex) {}
    }

    /**
     * creates a server to listen for incoming connections
     * May not be done if already attempted to connect
     * @throws java.io.IOException
     */
    public void listen() throws IOException {
        if (server == null && client == null) {
            server = new Server(this);
            server.listen();
        }
    }

    /**
     * creates a client connected to a server at address
     * @param address
     * @throws java.net.ConnectException
     */
    public void connect(String address) throws ConnectException {
        if (server == null && client == null) {
            try {
            client = new Client(this, address);
            } catch (ConnectException e) {
                client = null;
                throw e;
            }
            client.start();
        }
    }

    public void send(Message msg) {
        if (server != null) {
            msg.send(server);
        } else if (client != null) {
            msg.send(client);
        }
    }

    /**
     * receives all messages waiting to be received
     * @return if there were messages
     */
    public boolean receive() {
        boolean hadMsg = false;
        Message msg = receiveQueue.poll();
        while (msg != null) {
            hadMsg = true;
            if (server != null) {
                msg.receive(server);
            } else if (client != null) {
                msg.receive(client);
            }
            msg = receiveQueue.poll();
        }

        return hadMsg;
    }

    /**
     * closes connection(s) gracefully
     */
    public void close() {
        if (client != null) {
            QuitMessage qMsg = new QuitMessage();
            client.send(qMsg);
        }
        if (server != null) {
            server.close();
        }
    }

    /* getters and setters */
    public int getConnectionCount() {
        if (server != null) {
            return server.getConnectionCount();
        } else if (client != null) {
            return 1;
        } else {
            return 0;
        }
    }
    public int getPort() { return PORT; }
    public boolean isServer() { return server != null; }
    public boolean isClient() { return client != null; }
}
