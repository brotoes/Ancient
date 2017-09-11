/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import ancient.Main;
import ancient.buildings.BuildingFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.ArrayList;
import java.util.List;
import ancient.map.Province;
import mapGeneration.Selectable;
import ancient.pawns.Pawn;

/**
 * Controls the GUI
 * @author brock
 */
public class HudController implements ScreenController {
    private Nifty nifty;
    private Screen screen;

    /* vars to control info panels */
    private Selectable selected = null;
    private Element visInfoPanel = null;
    private Element provPanel;
    private Element pawnPanel;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        /* get info panels */
        provPanel = screen.findElementById("province_panel");
        pawnPanel = screen.findElementById("pawn_panel");
        provPanel.hide();
        pawnPanel.hide();
    }

    @Override
    public void onEndScreen() {

    }

    /**
     * Populates province panel and show it
     * @param prov
     */
    public void showInfoPanel(Province prov) {
        selected = prov;
        visInfoPanel = provPanel;
        Element title = visInfoPanel.findElementById("province_title");
        if (title != null) {
            title.getRenderer(TextRenderer.class).setText("Province " + prov.getId());
        }
        populateExistingBuildings(prov);
        populateAvailableBuildings(prov);


        visInfoPanel.show();
    }

    public void showInfoPanel(Pawn pawn) {
        selected = pawn;
        visInfoPanel = pawnPanel;
        visInfoPanel.show();
    }

    public void hideInfoPanel() {
        visInfoPanel.hide();
        visInfoPanel.setVisible(false);
        visInfoPanel = null;
        selected = null;
    }

    public void populateExistingBuildings(Province prov) {
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
        ArrayList<BuildingFactory> availFacs = BuildingFactory.getValidBuildingFactories(prov);
        if (list != null) {
            list.clear();
            for (int i = 0; i < availFacs.size(); i ++) {
                list.addItem(availFacs.get(i));          }
        }
    }

    /* functions for list interactions */
    public void existingBuildingClicked() {
        System.out.println("Existing");
    }

    public void availableBuildingClicked() {
        System.out.println("available");
    }

    /* functions for buttons */
    public void currencyButton() {

    }

    public void buildButton() {
        if (!(selected instanceof Province)) {
            System.err.println("Warning: Province panel shown then province not selected");
        }
        /* get selected building */
        ListBox buildingList = provPanel.findNiftyControl("avail_build_list", ListBox.class);
        if (buildingList == null) {
            System.err.println("Warning: no building list found");
            return;
        }
        List<BuildingFactory> selection = buildingList.getSelection();
        /* build that building */
        if (selection.isEmpty()) {
            return;
        }
        BuildingFactory fac = selection.get(0);
        ((Province)selected).addBuilding(fac.getBuilding((Province)selected));
        /*update the interface*/
        populateExistingBuildings((Province)selected);
        populateAvailableBuildings((Province)selected);
    }

    public void turnButton() {
        Main.app.getPlayState().getTurnController().dispatchNextTurn();
    }
}
