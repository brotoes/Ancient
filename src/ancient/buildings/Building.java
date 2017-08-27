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
import ancient.resources.Resource;
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
    private final List<ResourceContainer> resources = new ArrayList<>();
    private final List<ResourceContainer> forSale = new ArrayList<>();

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
        if (canProduce()) {
            reduceStored();
            storeProduction();
        }
        /* put produced resources up for sale */
        if (!forSale.isEmpty()) {
            for (ResourceContainer i : outs) {
                Seller seller = new Seller(this, i);
                Main.app.getPlayState().getTradeController().add(seller);
            }
        }
        printResources();
    }

    /**
     * returns true if has enough resources stores to produce
     * @return
     */
    public boolean canProduce() {
        for (ResourceContainer rc : ins) {
            boolean found = false;
            for (ResourceContainer storedRC : resources) {
                if (storedRC.getResource() == rc.getResource() &&
                        storedRC.getQty() >= rc.getQty()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * reduces the amount of resources stored according to inputs
     */
    public void reduceStored() {
        for (ResourceContainer rc : ins) {
            ResourceContainer.removeFromList(rc, resources);
        }
    }

    /**
     * adds the amount of resources stored according to outputs
     */
    public void storeProduction() {
        for (ResourceContainer rc : outs) {
            ResourceContainer.addToList(rc, forSale);
        }
    }

    /**
     * returns true if contains resource and qty specified
     * @param rc
     * @return
     */
    public boolean hasResource(ResourceContainer rc) {
        return hasResource(rc.getResource(), rc.getQty());
    }

    public boolean hasResource(Resource resource, int qty) {
        for (ResourceContainer rc : resources) {
            if (rc.getResource() == resource && rc.getQty() >= qty) {
                return true;
            }
        }
        return false;
    }

    public void addResource(ResourceContainer rc) {
        ResourceContainer.addToList(rc, resources);
    }

    /* getters and setters */
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public Province getProvince() { return prov; }
    public float getDistance(Building building) {
        return getProvince().getDistance(building.getProvince());
    }

    public void printResources() {
        System.out.println(name + "----");
        System.out.println("Stored:");
        for (ResourceContainer rc : resources) {
            System.out.println(rc);
        }
        System.out.println("For Sale: ");
        for (ResourceContainer rc : forSale) {
            System.out.println(rc);
        }
    }
}
