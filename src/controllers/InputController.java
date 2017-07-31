/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import appStates.PlayAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.CollisionResult;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import mapGeneration.Selectable;
import mapGeneration.SelectableNode;

/**
 * Controls all input for PlayState
 * @author brock
 */
public class InputController {
    private final PlayAppState state;
    private final InputManager im;
    private final CameraController camCon;
    
    private float zoomSpeed = 0.1f;
    private float scrollSpeed = 500.0f;
    
    private Selectable selected = null;
    
    public InputController(PlayAppState state) {
        this.state = state;
        im = state.getInputManager();
        camCon = state.getCameraController();
        
        im.setCursorVisible(true);
        im.clearMappings();
        
        //TODO: Load Config
        
        /* Add Mouse Mappings */
        im.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        im.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        
        /* Add Key Mappings */
        im.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        im.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        im.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        im.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        im.addMapping("MouseClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        
        /* Add Listeners */
        im.addListener(analogListener, "ZoomIn");
        im.addListener(analogListener, "ZoomOut");
        
        im.addListener(actionListener, "Left");
        im.addListener(actionListener, "Right");
        im.addListener(actionListener, "Up");
        im.addListener(actionListener, "Down");
        im.addListener(actionListener, "MouseClick");
    }
    
    protected void handleMouseClick(Vector2f pos) {
        //let GUI capture event
        //if GUI did not get event, check for provinces or pawns clicked
        CollisionResults results = new CollisionResults();
        Vector3f pos3d = camCon.getCamera().getWorldCoordinates(pos, 0f).clone();
        Vector3f dir = camCon.getCamera().getWorldCoordinates(pos, 1.0f).subtractLocal(pos3d).normalizeLocal();
        
        Ray ray = new Ray(pos3d, dir);
        
        state.getNode().collideWith(ray, results);
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            SelectableNode parent = (SelectableNode)closest.getGeometry().getParent();
            setSelected(parent.selectable);
        }
    }
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            float mod = -1.0f;
            if (keyPressed) {
                mod = 1.0f;
            }
            switch (name) {
                case "Up":
                    camCon.setSpeed(camCon.getSpeed().add(0, scrollSpeed*mod, 0));
                    break;
                case "Left":
                    camCon.setSpeed(camCon.getSpeed().add(-scrollSpeed*mod, 0, 0));
                    break;
                case "Right":
                    camCon.setSpeed(camCon.getSpeed().add(scrollSpeed*mod, 0, 0));
                    break;
                case "Down":
                    camCon.setSpeed(camCon.getSpeed().add(0, -scrollSpeed*mod, 0));
                    break;
                case "MouseClick":
                    if (keyPressed) {
                        handleMouseClick(im.getCursorPosition());
                    }
            }
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            switch (name) {
                case "ZoomIn":
                    state.getCameraController().changeZoomLevel(value*zoomSpeed);
                    break;
                case "ZoomOut":
                    state.getCameraController().changeZoomLevel(-value*zoomSpeed);
                    break;
            }
        }
    };
    
    public void setSelected(Selectable selected) {
        if (this.selected != null) {
            this.selected.deselect();
        }
        this.selected = selected;
        this.selected.select();
    }
    
    public Selectable getSelected() { return selected; }
}
