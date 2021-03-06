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
public class TerrainMapMode implements MapMode {
    @Override
    public Material getProvinceMaterial(Province prov) {
        Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        String fname = prov.getTerrainType().getTextureFile();
        if (fname == null) {
            mat.setColor("Diffuse", ColorRGBA.Magenta);
            mat.setBoolean("UseMaterialColors", true);
        } else {
            mat.setTexture("DiffuseMap",
                    Main.app.getAssetManager().loadTexture(prov.getTerrainType().getTextureFile()));
        }

        return mat;
    }

    @Override
    public String toString() { return "Terrain"; }
}
