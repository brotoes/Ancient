/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import ancient.Main;
import mapGeneration.Province;
import mapGeneration.Selectable;
import pawns.Pawn;

/**
 * Controls selections when a province is selected
 * @author brock
 */
public class ProvinceSelectionState extends SelectionState{
    public ProvinceSelectionState(Province prov) {
        super(prov);
    }
    
    @Override
    public void leftClick(Selectable clicked) {
        if (clicked instanceof Province) {
            if (getSelected().getId() == ((Province)clicked).getId()) {
                setState(new EmptySelectionState());
            } else {
                setState(new ProvinceSelectionState((Province)clicked));
            }
        } else if (clicked instanceof Pawn) {
            setState(new PawnSelectionState((Pawn)clicked));
        }
    }
    
    @Override
    public void rightClick(Selectable clicked) {
    
    }
    
    @Override
    protected void set() {
        super.set();
        Main.app.getGuiController().showInfoPanel(getSelected());
    }
    
    @Override
    protected void unset() {
        super.unset();
        Main.app.getGuiController().hideInfoPanel();
    }
    
    @Override
    public Province getSelected() {
        return (Province)super.getSelected();
    }
}
