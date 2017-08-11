/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import mapGeneration.Province;
import mapGeneration.Selectable;
import pawns.Pawn;

/**
 * Controls the GUI
 * @author brock
 */
public class GuiController extends AbstractAppState implements ScreenController {
    private Nifty nifty;
    private Screen screen;
    
    /* vars to control info panels */
    private Selectable selected = null;
    private Element visInfoPanel = null;
    private Element provPanel;
    private Element pawnPanel;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        /* get info panels */
        provPanel = screen.findElementById("province_panel");
        pawnPanel = screen.findElementById("pawn_panel");
        provPanel.hide();
        pawnPanel.hide();
    }
    
    @Override
    public void update(float tpf) {
        //TODO:
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }
    
    @Override
    public void onStartScreen() {
        
    }
    
    @Override
    public void onEndScreen() {
        
    }
    
    public void click() {

    }
    
    public void showInfoPanel(Selectable item) {
        selected = item;
        if (item instanceof Province) {
            visInfoPanel = provPanel;
        } else if (item instanceof Pawn) {
            visInfoPanel = pawnPanel;
        }
        visInfoPanel.show();
    }
    
    public void hideInfoPanel() {
        visInfoPanel.hide();
        visInfoPanel.setVisible(false);
        visInfoPanel = null;
        selected = null;
    }
}
