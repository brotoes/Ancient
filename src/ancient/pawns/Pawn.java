package ancient.pawns;


import ancient.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import controllers.game.TurnListener;
import java.util.ArrayList;
import ancient.map.Province;
import ancient.resources.Resource;
import ancient.resources.ResourceContainer;
import mapGeneration.Selectable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author brock
 */
public class Pawn implements Selectable, TurnListener {
    private static int nextId = 0;

    private final int id;
    private final Node pivot;
    private final Geometry geom;
    private final Box box;
    private final Material mat;
    private final ColorRGBA color = ColorRGBA.Red;
    private final ColorRGBA selectedColor = ColorRGBA.White;
    private final ResourceContainer resourceContainer;

    private ArrayList<Province> path = null;
    private Province province;
    private Geometry pathGeom;

    public static Pawn newPawn(Province prov, Resource resource, int qty) {
        return Pawn.newPawn(prov, new ResourceContainer(resource, qty));
    }

    public static Pawn newPawn(Province prov, ResourceContainer resource) {
        Pawn pawn = new Pawn(prov, resource);
        Main.app.getPlayState().getTurnController().addListener(pawn);
        prov.addPawn(pawn);
        return pawn;
    }

    /**
     * Spawns a new pawn at province
     * @param province
     * @param resourceContainer
     */
    public Pawn(Province province, ResourceContainer resourceContainer) {
        this.id = Pawn.nextId;
        Pawn.nextId ++;
        this.province = province;
        pivot = new Node("pivot");
        Main.app.getPlayState().getNode().attachChild(pivot);

        box = new Box(0.4f,0.4f,0.4f);
        geom = new Geometry("geom", box);
        mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", color);
        mat.setParam("UseMaterialColors", VarType.Boolean, true);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0, 0, 0.5f);

        geom.setUserData("clickTarget", new Selectable[]{this});
        pivot.attachChild(geom);
        pivot.setLocalTranslation(province.getPivot().getLocalTranslation());

        this.resourceContainer = resourceContainer;
    }

    @Override
    public void select() {
        showPathGeom();
        mat.setColor("Diffuse", selectedColor);
    }

    @Override
    public void deselect() {
        hidePathGeom();
        mat.setColor("Diffuse", color);
    }

    @Override
    public void nextTurn() {
        move();
    }

    /**
     * Move to next province in path
     */
    public void move() {
        if (path == null) {
            return;
        }
        /* remove from current province and add to next province in path */
        path.get(0).removePawn(this);
        path.get(1).addPawn(this);
        province = path.get(1);
        path.remove(0);
        if (path.size() <= 1) {
            path = null;
        }
        pivot.setLocalTranslation(province.getPivot().getLocalTranslation());
        if (pathGeom != null) {
            showPathGeom();
        }
    }

    public void showPathGeom() {
        if (pathGeom != null) {
            hidePathGeom();
        }
        if (path != null) {
            pathGeom = province.getPathGeom(path);
            Main.app.getPlayState().getTopNode().attachChild(pathGeom);
        }
    }

    public void hidePathGeom() {
        if (pathGeom != null) {
            Main.app.getPlayState().getTopNode().detachChild(pathGeom);
            pathGeom = null;
        }
    }

    /* Getters and setters */
    public Province getProvince() { return province; }
    public int getId() { return id; }
    public void setDestination(Province dest) { path = dest.getPath(province); }
    public Province getDestination() { return path.get(path.size() - 1); }
    public ResourceContainer getResourceContainer() { return resourceContainer; }
    public Resource getResource() { return resourceContainer.getResource(); }
}
