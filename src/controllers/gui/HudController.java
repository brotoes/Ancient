/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import ancient.Main;
import controllers.mapModes.MapMode;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * Controls the GUI
 * @author brock
 */
public class HudController implements ScreenController {
    private Nifty nifty;
    private Screen screen;

    /* vars to control info panels */
    //private Selectable selected;
    private Infoable infoPanel;
    private Element infoPanelContainer;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        try {
            Element dropElem = screen.findElementById("map_mode_dropdown");
            DropDown modeDrop = dropElem.getNiftyControl(DropDown.class);
            modeDrop.addAllItems(Main.app.getPlayState().getMapModeController().getMapModes());
            modeDrop.selectItemByIndex(0);
        } catch (NullPointerException e) {
            System.err.println("Error referencing drop down");
        }
    }

    @Override
    public void onStartScreen() {
        infoPanelContainer = screen.findElementById("infopanel_container");
    }

    @Override
    public void onEndScreen() {}

    /**
     * Get and display the info panel
     * @param obj
     */
    public void showInfoPanel(Infoable obj) {
        infoPanel = obj;
        try {
            //selected = prov;
            infoPanel.showInfoPanel(nifty, screen, infoPanelContainer);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * removes info panel from infopanelcontainer
     */
    public void hideInfoPanel() {
        infoPanel.removeInfoPanel();
        infoPanel = null;
    }

    /**
     * Passes click event for an info panel through to the info panel
     * @param args
     */
    public void infoClick(String args) {
        String[] argarr = args.split("\\|");
        if (infoPanel != null) {
            infoPanel.infoClick(argarr);
        }
    }

    @NiftyEventSubscriber(id = "map_mode_dropdown")
    public void mapModeSelection(String id, DropDownSelectionChangedEvent<? extends MapMode> e) {
        Main.app.getPlayState().getMapModeController().setMapMode(e.getSelection());
    }

    /* functions for buttons */
    public void currencyButton() {

    }

    public void turnButton() {
        Main.app.getPlayState().getTurnController().dispatchNextTurn();
    }
}
