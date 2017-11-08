/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.mapModes;

import ancient.map.Province;
import com.jme3.material.Material;

/**
 *
 * @author brock
 */
public interface MapMode {
    public Material getProvinceMaterial(Province prov);
}
