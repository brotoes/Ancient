/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

import java.net.ConnectException;

/**
 *
 * @author brock
 */
public class JoinException extends Exception {
    public JoinException(ConnectException e) {
        super("Failed to join game. Could not connect to server.");
    }

    public JoinException() {
        super("Failed to join game");
    }
}
