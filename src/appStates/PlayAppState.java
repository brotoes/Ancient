/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appStates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import controllers.CameraController;
import controllers.InputController;
import mapGeneration.WorldMap;

/**
 *
 * @author brock
 */
public class PlayAppState extends AbstractAppState {
    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    
    private final Node node = new Node("stateNode");
    private CameraController camCon;
    private InputController inputCon;
    private WorldMap worldMap;
    
    public PointLight camLight;
  
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.rootNode     = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort     = this.app.getViewPort();
        
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        
        app.getCamera().setLocation(new Vector3f(500, 400, 250));
        app.getCamera().setFrustumFar(10000);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1.0f, 0.1f, -5.0f).normalize());
        sun.setColor(ColorRGBA.White);
        
        camLight = new PointLight();
        camLight.setRadius(500.0f);
        camLight.setColor(ColorRGBA.White);
        
        node.addLight(sun);
        node.addLight(camLight);
        
        worldMap = new WorldMap();
        camCon = new CameraController(this);
        inputCon = new InputController(this);
        
        enable();
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        // unregister all listeners, detach nodes etc
        this.app.getRootNode().detachChild(node);
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
    
    private void enable() {
        rootNode.attachChild(node);
    }
    
    @Override
    public void update(float tpf) {
        camCon.tick(tpf);
    }
    
    /* Getters and Setters */
    public SimpleApplication getApp() { return app; }
    public Node getNode() { return node; }
    public InputManager getInputManager() { return inputManager; }
    public CameraController getCameraController () { return camCon; }
}
