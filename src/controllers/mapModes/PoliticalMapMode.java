/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.mapModes;

import ancient.Main;
import ancient.map.Province;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author brock
 */
public class PoliticalMapMode implements MapMode {
    @Override
    public Material getProvinceMaterial(Province prov) {
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        if (prov.getTerrainType().getName().equals("Water")) {
            mat.setTexture("DiffuseMap",
                    Main.app.getAssetManager().loadTexture("Textures/waterTexture.png"));
        } else if (prov.getOwner() == null) {
            mat.setBoolean("UseMaterialColors", true);
            mat.setColor("Diffuse", ColorRGBA.DarkGray);
        } else {
            mat.setBoolean("UseMaterialColors", true);
            mat.setColor("Diffuse", prov.getOwner().getColor());
        }

        return mat;
    }

    @Override
    public String toString() { return "Political"; }
}
