/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.ui;

import map.Province;
import mapGeneration.Selectable;
import pawns.Pawn;

/**
 *
 * @author brock
 */
public class EmptySelectionState extends SelectionState {
    
    public EmptySelectionState() {
        super(null);
    }
    
    @Override
    public void leftClick(Selectable clicked) {
        if (clicked instanceof Province) {
            getCon().setState(new ProvinceSelectionState((Province)clicked));
        } else if (clicked instanceof Pawn) {
            getCon().setState(new PawnSelectionState((Pawn)clicked));
        }
    }
    
    @Override
    public void rightClick(Selectable clicked) {}

    @Override
    public void unset() {}

    @Override
    public void set() {}
    
}
