/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import ancient.Main;
import com.jme3.scene.Geometry;
import mapGeneration.Province;
import mapGeneration.Selectable;

/**
 * Controls selections when a province is selected
 * @author brock
 */
public class ProvinceSelectionState extends SelectionState{
    public ProvinceSelectionState(Province prov) {
        super(prov);
    }
    
    @Override
    public void select(Selectable selected) {
        //USE THIS CODE LATER
        /*if (selected instanceof Province) {
            if (getSelected().hashCode() == selected.hashCode()
                    && getSelected() == selected) {
                setState(new EmptySelectionState());
            } else {
                setState(new ProvinceSelectionState((Province)selected));
            }
        }*/
        Geometry geom = ((Province)selected).getPathGeom(getSelected());
        Main.app.getPlayState().getNode().attachChild(geom);
        setState(new EmptySelectionState());
    }

    @Override
    public void unset() {
        getSelected().deselect();
    }

    @Override
    public void set() {
        getSelected().select();
    }
    
    @Override
    public Province getSelected() {
        return (Province)super.getSelected();
    }
}
