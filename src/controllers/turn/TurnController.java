/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.turn;

import java.util.ArrayList;

/**
 * Watches for when the turn is done. Dispatches a turn event
 * @author brock
 */
public class TurnController {
    private final ArrayList<TurnListener> turnListeners;

    public TurnController() {
        turnListeners = new ArrayList<>();
    }

    public void dispatchNextTurn() {
        for (TurnListener i : turnListeners) {
            i.nextTurn();
        }
    }

    public void addListener(TurnListener listener) {
        turnListeners.add(listener);
    }
}
