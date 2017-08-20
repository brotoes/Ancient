/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildings;

import java.util.List;
import map.Province;

/**
 *
 * @author brock
 */
public class Building {
    private final String name;
    private final String desc;
    private final Province prov;
    private final List<String> ins;
    private final List<String> outs;
    private int money = 10;

    protected Building(String name, String desc, Province prov,
            List<String> ins, List<String> outs) {
        this.name = name;
        this.desc = desc;
        this.prov = prov;
        this.ins = ins;
        this.outs = outs;
    }

    @Override
    public String toString() { return getName(); }

    public void produce() {
        /*Pawn pawn = new Pawn(prov);
        Main.app.getPlayState().getTurnController().addListener(pawn);
        prov.addPawn(pawn);*/

        /* make requests for needed resources */
        /* produce resources */
        /* put produced resources up for sale */
    }

    /* getters and setters */
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public Province getProvince() { return prov; }
    public float getDistance(Building building) {
        return getProvince().getDistance(building.getProvince());
    }
}
