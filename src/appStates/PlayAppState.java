/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appStates;

import ancient.Main;
import pawns.Pawn;
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
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import controllers.ui.CameraController;
import controllers.ui.InputController;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;
import map.WorldMap;

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
    
    private Nifty nifty;
    private CameraController camCon;
    private InputController inputCon;
    private WorldMap worldMap;
    private final ArrayList<Pawn> pawns = new ArrayList<>();
    
    public PointLight camLight;
    
    /** spatials added to this node render after and without depth testing */
    private final Node topNode = new Node("topNode");
    /** when added to this node, spatials render normally */
    private final Node node = new Node("stateNode");
  
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.rootNode     = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort     = this.app.getViewPort();
        
        /* Set up camera */
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        
        app.getCamera().setLocation(new Vector3f(50, 40, 25));
        app.getCamera().setFrustumFar(1000);
        
        /* Set up Lighting */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1.0f, 0.1f, -5.0f).normalize());
        sun.setColor(ColorRGBA.White);
        
        camLight = new PointLight();
        camLight.setRadius(100.0f);
        camLight.setColor(ColorRGBA.White);
        
        node.addLight(sun);
        node.addLight(camLight);
        
        /* set up the scene */
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
        
        for (int i = 0; i < worldMap.getNumProvs(); i ++) {
            worldMap.getProvince(i).step();
        }
        
        for (Pawn i : pawns) {
            //i.update();
        }
    }
    
    @Override
    public void render(RenderManager rm) {
        rm.renderScene(topNode, viewPort);
    }
    
    /**
     * Adds pawns. TODO: REMOVE
     * @return 
     */
    public void addPawn(Pawn pawn) {
        pawns.add(pawn);
    }
    
    /* Getters and Setters */
    public SimpleApplication getApp() { return app; }
    public Node getNode() { return node; }
    public Node getTopNode() { return topNode; }
    public InputManager getInputManager() { return inputManager; }
    public CameraController getCameraController () { return camCon; }
}
