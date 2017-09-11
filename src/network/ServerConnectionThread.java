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

/**
 *
 * @author brock
 */
class ServerConnectionThread extends Thread {
    private Socket sock;

    protected ServerConnectionThread(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        BufferedReader is;
        PrintWriter os;

        try{
            is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            os = new PrintWriter(sock.getOutputStream());
        } catch (IOException e) {
            return;
        }

        String line;
        while(true) {
            try {
                line = is.readLine();
                System.out.println(line);
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    sock.close();
                    return;
                } else {
                    os.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
