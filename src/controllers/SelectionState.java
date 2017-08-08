/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import mapGeneration.Selectable;

/**
 *
 * @author brock
 */
public abstract class SelectionState {
    private static SelectionController con;
    private final Selectable selected;
    
    public static void setController(SelectionController con) {
        SelectionState.con = con;
    }
    
    public SelectionState(Selectable selected) {
        this.selected = selected;
    }
    
    public Selectable getSelected() {
        return selected;
    }
    
    protected SelectionController getCon() {
        return SelectionState.con;
    }
    
    protected void setState(SelectionState state) {
        getCon().setState(state);
    }
    
    /**
     * When something is clicked/selected, this method is called
     * @param selected 
     */
    public abstract void select(Selectable selected);
    
    /**
     * called when unsetting this state
     */
    public abstract void unset();
    
    /** 
     * called when setting this state
     */
    public abstract void set();
}
