/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.mapModes;

import ancient.Main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author brock
 */
public class MapModeController {
    private List<MapMode> modes;
    private int curMode;

    public MapModeController() {
        modes = new ArrayList<>();
        modes.add(new TerrainMapMode());
        modes.add(new PoliticalMapMode());
    }

    public void setMapMode(int index) {
        curMode = index;
        Main.app.getPlayState().getWorldMap().getProvinces().stream().forEach(p -> {
            p.setFaceMaterial(getActiveMapMode().getProvinceMaterial(p));
        });
    }
    public void setMapMode(MapMode mode) {
        setMapMode(modes.indexOf(mode));
    }

    /* getters and setters */
    public List<MapMode> getMapModes() { return Collections.unmodifiableList(modes); }

    public MapMode getActiveMapMode() { return modes.get(curMode); }
}
