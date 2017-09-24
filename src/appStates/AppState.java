/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appStates;

import ancient.Main;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import controllers.interaction.PlayInputController;
import de.lessvoid.nifty.Nifty;

/**
 * Controls game behaviour while in menus
 * @author brock
 */
public class AppState extends AbstractAppState {
    protected SimpleApplication app;
    protected Node rootNode;
    protected AssetManager assetManager;
    protected AppStateManager stateManager;
    protected InputManager inputManager;
    protected ViewPort viewPort;

    protected Nifty nifty;
    protected PlayInputController inputCon;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.rootNode     = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort     = this.app.getViewPort();
        this.nifty        = Main.app.getNifty();

        Main.app.getInputManager().setCursorVisible(true);
        Main.app.getInputManager().clearMappings();

        enable();
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            enable();
        } else {
            //Pause the state
        }
    }

    protected void enable() {}

    @Override
    public void update(float tpf) {
        Main.app.getNetworkController().update();
    }

    /* Getters and Setters */
    public SimpleApplication getApp() { return app; }
    public InputManager getInputManager() { return inputManager; }
}
