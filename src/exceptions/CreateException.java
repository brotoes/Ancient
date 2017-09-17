/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

import java.io.IOException;

/**
 *
 * @author brock
 */
public class CreateException extends Exception {
    public CreateException(IOException e) {
        super("Could not create game. Network error.");
    }

    public CreateException() {
        super("Could not create game.");
    }
}
