/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import network.exceptions.MalformedMessageException;

/**
 * Takes a socket to accept and send messages.
 * if interrupted, will close.
 * @author brock
 */
public class Connection extends Thread {
    private final long POLLRATE = 10;

    private final Socket sock;
    private BufferedReader is;
    private PrintWriter os;

    private final BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();
    private final MessageManager messageManager;

    private boolean stopped = false;

    /**
     * creates connection to be run with start()
     * @param sock this socket will be used for sending and receivng messages
     * @param msgMgr
     */
    protected Connection(Socket sock, MessageManager msgMgr) {
        this.sock = sock;
        this.messageManager = msgMgr;
    }

    @Override
    public void run() {
        try{
            is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            os = new PrintWriter(sock.getOutputStream());
        } catch (IOException e) {
            return;
        }

        /* poll for stuff to do */
        String line;
        while(true) {
            try {
                /* check for a message ready to send out */
                line = sendQueue.poll(POLLRATE, TimeUnit.MILLISECONDS);
                if (line != null) {
                    os.println(line);
                    os.flush();

                    if (line.equals("QUIT")) {
                        break;
                    }
                }
                /* check for an incoming message */
                if (is.ready()) {
                    line = is.readLine();
                    messageManager.parse(this, line);
                    if (line.equals("QUIT")) {
                        break;
                    }
                }
                /* close thread on an interrupt */
                if (Thread.interrupted()) {
                    break;
                }
            } catch (IOException | MalformedMessageException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {}
            if (stopped) {
                break;
            }
        }
        close(true);
    }

    protected void send(String line) {
        try {
            sendQueue.put(line);
        } catch (InterruptedException e) {
            close(true);
        }
    }

    private void close(boolean thread) {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("closed connection");
    }
}
