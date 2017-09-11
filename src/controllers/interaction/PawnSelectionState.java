/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.interaction;

import ancient.Main;
import ancient.map.Province;
import mapGeneration.Selectable;
import ancient.pawns.Pawn;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * Controls selections when a province is selected
 * @author brock
 */
public class PawnSelectionState extends SelectionState{
    public PawnSelectionState(Pawn pawn) {
        super(pawn);
    }

    @Override
    public void leftClick(Selectable clicked) {
        if (clicked instanceof Province) {
            setState(new ProvinceSelectionState((Province)clicked));
        } else if (clicked instanceof Pawn) {
            if (getSelected().getId() == ((Pawn)clicked).getId()) {
                setState(new EmptySelectionState());
            } else {
                setState(new PawnSelectionState((Pawn)clicked));
            }
        }
    }

    @Override
    public void rightClick(Selectable clicked) {
        Province prov;
        if (clicked instanceof Province) {
            prov = (Province)clicked;
        } else if (clicked instanceof Pawn) {
            prov = ((Pawn) clicked).getProvince();
        } else {
            return;
        }
        getSelected().setDestination(prov);
        getSelected().showPathGeom();
    }

    @Override
    protected void set() {
        super.set();
        Main.app.getPlayState().getHudController().showInfoPanel(getSelected());
    }

    @Override
    protected void unset() {
        super.unset();
        Main.app.getPlayState().getHudController().hideInfoPanel();
    }

    @Override
    public Pawn getSelected() {
        return (Pawn)super.getSelected();
    }
}
