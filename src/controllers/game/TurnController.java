/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.game;

import actionTracker.ActionTracker;
import ancient.Main;
import controllers.network.messages.ActionsMessage;
import java.util.ArrayList;
import network.messages.Message;

/**
 * Watches for when the turn is done. Dispatches a turn event
 * @author brock
 */
public class TurnController {
    private final ArrayList<TurnListener> turnListeners;
    private final ArrayList<TurnListener> addQueue;
    private boolean dispatching = false;

    public TurnController() {
        turnListeners = new ArrayList<>();
        addQueue = new ArrayList<>();
    }

    public void nextTurn() {
        if (Main.app.getNetworkController().isHost()) {
            dispatchNextTurn();
        } else {
            ActionTracker at = Main.app.getPlayState().getActionTracker();
            Message msg = new ActionsMessage(at);
            Main.app.getNetworkController().send(msg);

            at.clear();
        }
    }

    public void dispatchNextTurn() {
        dispatching = true;
        for (TurnListener i : turnListeners) {
            i.nextTurn();
        }
        dispatching = false;
        processAddQueue();

        /* do end of turn stuff */
        /* process trades */
        Main.app.getPlayState().getTradeController().processTrades();
        if (!Main.app.getNetworkController().isHost()) {
            Message msg = new ActionsMessage(Main.app.getPlayState().getActionTracker());
            Main.app.getNetworkController().send(msg);
        }

        /* unset player ready status */
        Main.app.getPlayerManager().getPlayers().stream().forEach(p -> p.setReady(false));
    }

    /**
     * Adds a listeners to the queue to be added,
     * processes the add queue if not already dispatching.
     * @param listener
     */
    public void addListener(TurnListener listener) {
        if (addQueue.indexOf(listener) < 0 &&
                turnListeners.indexOf(listener) < 0) {
            addQueue.add(listener);
            processAddQueue();
        }
    }

    /**
     * to prevent listeners added during dispatching,
     * add to a queue first and then put the queue into turn listeners.
     */
    private void processAddQueue() {
        if (!dispatching) {
            for (TurnListener i : addQueue) {
                turnListeners.add(i);
            }
            addQueue.clear();
        }
    }
}
