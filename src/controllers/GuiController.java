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
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * Controls the GUI
 * @author brock
 */
public class GuiController extends AbstractAppState implements ScreenController {
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO
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

    }
    
    @Override
    public void onStartScreen() {
        
    }
    
    @Override
    public void onEndScreen() {
        
    }
    
    public void click() {
        System.out.println("Click");
    }
}
