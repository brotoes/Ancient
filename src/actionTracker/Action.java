/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actionTracker;

/**
 * Base class for an Action, a reproduceable operation.
 * @author brock
 */
public abstract class Action {
    /**
     * using action data, reproduces this action
     */
    void perform() {
        throw new UnsupportedOperationException("Action cannot be performed");
    }

    /**
     * using action data, undoes this action
     */
    void undo() {
        throw new UnsupportedOperationException("Action cannot be undone");
    }
}
