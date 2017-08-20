/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.trade;

import buildings.Building;
import hungarian.Matchable;

/**
 *
 * @author brock
 */
public class Buyer implements Matchable {
    private final Building building;
    /* Quantity desired */
    private final int qty;
    private final int maxPrice;
    // TEMPORARY
    private final String resource;

    private Seller seller = null;

    public Buyer(Building b, int qty, int max, String resource) {
        this.building = b;
        this.qty = qty;
        this.maxPrice = max;
        this.resource = resource;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "Buying " + qty + " " + resource + " for " + maxPrice;
    }

    @Override
    public int getCost(Matchable obj) {
        if (obj instanceof Seller) {
            return ((Seller)obj).getCost(this);
        } else {
            System.err.println("Warning: Buyer matched with non-Seller");
            return -1;
        }
    }

    @Override
    public void setMatch(Matchable match) {
        if (match instanceof Seller) {
            this.seller = (Seller)match;
        } else {
            System.err.println("Warning: Buyer matched with non-Seller");
        }
    }

    /* getters and setters */
    public Building getBuilding() { return building; }
}
