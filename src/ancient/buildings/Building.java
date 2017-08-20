/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.buildings;

import ancient.Main;
import controllers.trade.Buyer;
import controllers.trade.Seller;
import java.util.List;
import ancient.map.Province;
import ancient.resources.ResourceContainer;
import java.util.ArrayList;

/**
 *
 * @author brock
 */
public class Building {
    private final String name;
    private final String desc;
    private final Province prov;
    /* stores required production ratios in the form of ResourceContainers */
    private final List<ResourceContainer> ins;
    private final List<ResourceContainer> outs;

    private int money = 10;
    private final List<ResourceContainer> resources = new ArrayList<ResourceContainer>();

    protected Building(String name, String desc, Province prov,
            List<ResourceContainer> ins, List<ResourceContainer> outs) {
        this.name = name;
        this.desc = desc;
        this.prov = prov;
        this.ins = ins;
        this.outs = outs;
    }

    @Override
    public String toString() { return getName(); }

    /**
     * produces, buys, and sells resources
     */
    public void produce() {
        /* make requests for needed resources */
        for (ResourceContainer i : ins) {
            Buyer buyer = new Buyer(this, i);
            Main.app.getPlayState().getTradeController().add(buyer);
        }
        /* produce resources */
        if (storedIn > 0 || ins.isEmpty()) {
            storedOut ++;
            if (!outs.isEmpty()) {
                storedIn --;
            }
        }
        /* put produced resources up for sale */
        if (storedOut > 0) {
            for (ResourceContainer i : outs) {
                Seller seller = new Seller(this, i);
                Main.app.getPlayState().getTradeController().add(seller);
            }
        }
    }

    /* getters and setters */
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public Province getProvince() { return prov; }
    public float getDistance(Building building) {
        return getProvince().getDistance(building.getProvince());
    }
}
