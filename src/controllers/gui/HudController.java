/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import ancient.Main;
import de.lessvoid.nifty.Nifty;
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
    public void infoClick(String... args) {
        if (infoPanel != null) {
            infoPanel.infoClick(args);
        }
    }

    /*public void populateExistingBuildings(Province prov) {
        ListBox list = provPanel.findNiftyControl("exist_build_list", ListBox.class);
        if (list == null) {
            System.err.println("Warning: No Existing Building List");
            return;
        }
        list.clear();
        for (int i = 0; i < prov.getNumBuildings(); i ++) {
            list.addItem(prov.getBuilding(i));
        }
    }

    public void populateAvailableBuildings(Province prov) {
        ListBox list = provPanel.findNiftyControl("avail_build_list", ListBox.class);
        List<BuildingFactory> availFacs = BuildingFactory.getValidBuildingFactories(prov);
        if (list != null) {
            list.clear();
            for (int i = 0; i < availFacs.size(); i ++) {
                list.addItem(availFacs.get(i));          }
        }
    }*/

    /* functions for list interactions */
    /*public void existingBuildingClicked() {
        System.out.println("Existing");
    }

    public void availableBuildingClicked() {
        System.out.println("available");
    }*/

    /* functions for buttons */
    public void currencyButton() {

    }

    /*public void buildButton() {
        if (!(selected instanceof Province)) {
            System.err.println("Warning: Province panel shown then province not selected");
        }
        /* get selected building
        ListBox buildingList = provPanel.findNiftyControl("avail_build_list", ListBox.class);
        if (buildingList == null) {
            System.err.println("Warning: no building list found");
            return;
        }
        List<BuildingFactory> selection = buildingList.getSelection();
        /* build that building
        if (selection.isEmpty()) {
            return;
        }
        BuildingFactory fac = selection.get(0);
        ((Province)selected).addBuilding(fac.getBuilding((Province)selected));
        /*update the interface
        populateExistingBuildings((Province)selected);
        populateAvailableBuildings((Province)selected);
    }*/

    public void turnButton() {
        Main.app.getPlayState().getTurnController().dispatchNextTurn();
    }
}
