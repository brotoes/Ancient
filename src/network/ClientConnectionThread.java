/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import de.lessvoid.nifty.elements.render.TextRenderer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author brock
 */
class ClientConnectionThread extends Thread {
    private Socket sock;
    private TextRenderer output;
    private BufferedReader is;
    private PrintWriter os;

    protected ClientConnectionThread(Socket sock, TextRenderer output) {
        this.sock = sock;
        this.output = output;

        try{
            is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            os = new PrintWriter(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                String line = is.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    sock.close();
                    return;
                } else {
                    output.setText(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void send(String line) {
        os.println(line);
    }
}
