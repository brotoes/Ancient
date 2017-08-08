/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import mapGeneration.Province;
import mapGeneration.Selectable;

/**
 *
 * @author brock
 */
public class EmptySelectionState extends SelectionState {
    
    public EmptySelectionState() {
        super(null);
    }
    
    @Override
    public void select(Selectable selected) {
        if (selected instanceof Province) {
            getCon().setState(new ProvinceSelectionState((Province)selected));
        }
    }

    @Override
    public void unset() {}

    @Override
    public void set() {}
    
}
