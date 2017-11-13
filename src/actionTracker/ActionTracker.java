/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actionTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks game actions in a actions.
 * @author brock
 */
public class ActionTracker {
    private final List<Action> actions;

    public ActionTracker() {
        actions = new ArrayList<>();
    }

    /**
     * performs and action and adds it to the list of actions.
     * @param action
     */
    public void act(Action action) {
        actions.add(action);
        action.perform();
    }

    /**
     * removes the first action from the queue and performs it.
     * @return if there are remaining actions
     */
    public boolean reproduce() {
        Action action = actions.remove(0);
        action.perform();
        return !actions.isEmpty();
    }

    /**
     * removes the latest action from the queue and undoes it.
     * @return true if there are remaining actions
     */
    public boolean undo() {
        Action action = actions.remove(actions.size() - 1);
        action.undo();
        return !actions.isEmpty();
    }

    /**
     * clears all actions
     */
    public void clear() {
        actions.clear();
    }

    /* getters and setters */
    public List<Action> getActions() { return Collections.unmodifiableList(actions); }
}
