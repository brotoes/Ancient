/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.ui;

import mapGeneration.Selectable;

/**
 * Controls what happens when selectables are selected.
 * @author brock
 */
public class SelectionController {
    private SelectionState state;
    
    public SelectionController() {
        state = new EmptySelectionState();
        SelectionState.setController(this);
    }
    
    public void click(Selectable clicked, boolean left) {
        if (left) {
            state.leftClick(clicked);
        } else {
            state.rightClick(clicked);
        }
    }
    
    public void setState(SelectionState newState) {
        state.unset();
        state = newState;
        state.set();
    }
    
    public Selectable getSelected() {
        return state.getSelected();
    }
}
