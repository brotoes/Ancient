/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.ui;

import ancient.Main;
import appStates.PlayAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import utils.MathUtils;

/**
 *
 * @author brock
 */
public class CameraController {
    private final PlayAppState state;
    private final Camera cam;
    
    private final float closeRatio = 0.5f;
    private final float farRatio = 1.0f;
    private final float closeDist = 20.0f;
    private final float farDist = 100.0f;
    private final float closeY = -20.0f;
    private final float farY = 0.0f;
    
    private final Vector3f camLeft = new Vector3f(-1.0f, 0.0f, 0.0f);
   
    private Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f); 
    private Vector3f camSpeed = new Vector3f(0, 0, 0);
    private Vector3f camPos = new Vector3f(50.f, 40.0f, 0.0f);
    private float zoomLevel = 0.5f;
    
    public CameraController(PlayAppState state) {
        this.state = state;
        cam = state.getApp().getCamera();

        updateCam();
    }
    
    public void tick(float tpf) {
        shiftPos(camSpeed.mult(tpf));
    }
    
    private void updateCam() {  
        float dist = MathUtils.map(zoomLevel, farDist, closeDist);
        float ratio = MathUtils.map(zoomLevel, farRatio, closeRatio);
        float yOffset = MathUtils.map(zoomLevel, farY, closeY);
        
        Vector3f camDir = new Vector3f(0.0f, 1 - ratio, -ratio).normalize();
        Vector3f yOffsetVec = new Vector3f(0.0f, yOffset, 0.0f);
        
        cam.setLocation(camPos.setZ(dist).add(yOffsetVec));
        Main.app.getPlayState().camLight.setPosition(cam.getLocation().clone().setZ(20.0f));
        camUp = camDir.cross(camLeft).normalizeLocal();
        cam.setAxes(camLeft, camUp, camDir);
    }
    
    public void setSpeed(Vector3f speed) {
        camSpeed = speed.setZ(0);
    }
    
    public Vector3f getSpeed() { return camSpeed; }
    
    public void setPos(Vector3f vec) {
        camPos = vec.setZ(0.0f);

        updateCam();
    }
    
    public void shiftPos(Vector3f shift) {
        setPos(camPos.add(shift.setZ(0)));
    }
    
    public void shiftPos(float x, float y) {
        setPos(camPos.add(new Vector3f(x, y, 0.0f)));
    }
    
    public void changeZoomLevel(float diff) {
        setZoomLevel(zoomLevel + diff);
    }
    
    public void setZoomLevel(float lvl) {
        if (lvl > 1.0f) {
            lvl = 1.0f;
        } else if (lvl < 0.0f) {
            lvl = 0.0f;
        }
        zoomLevel = lvl;

        updateCam();
    }
    
    public float getZoomLevel() {
        return zoomLevel;
    }
    
    public Camera getCamera() { return cam; }
}
