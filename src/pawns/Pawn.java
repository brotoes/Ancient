package pawns;


import ancient.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import map.Province;
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
public class Pawn implements Selectable {
    private static int nextId = 0;
    
    private final int id;
    private final Province province;
    private final Node pivot;
    private final Geometry geom;
    private final Box box;
    private final Material mat;
    private final ColorRGBA color = ColorRGBA.Red;
    private final ColorRGBA selectedColor = ColorRGBA.White;
    
    /**
     * Spawns a new pawn at province
     * @param province
     */
    public Pawn(Province province) {
        this.id = Pawn.nextId;
        Pawn.nextId ++;
        this.province = province;
        pivot = new Node("pivot");
        Main.app.getPlayState().getNode().attachChild(pivot);
        
        box = new Box(1,1,1);
        geom = new Geometry("geom", box);
        mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", color);
        mat.setParam("UseMaterialColors", VarType.Boolean, true);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0, 0, 0.5f);
        
        geom.setUserData("clickTarget", new Selectable[]{this});
        pivot.attachChild(geom);
        pivot.setLocalTranslation(province.getPivot().getLocalTranslation());
    }

    @Override
    public void select() {
        mat.setColor("Diffuse", selectedColor);
    }

    @Override
    public void deselect() {
        mat.setColor("Diffuse", color);
    }
    
    /* Getters and setters */
    public Province getProvince() { return province; }
    public int getId() { return id; }
}
