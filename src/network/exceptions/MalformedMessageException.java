/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.exceptions;

/**
 *
 * @author brock
 */
public class MalformedMessageException extends Exception {
    public MalformedMessageException() {
        super("Malformed Message.");
    }

    public MalformedMessageException(String msg) {
        super(msg);
    }

    public MalformedMessageException(Exception e) {
        super(e);
    }
}
