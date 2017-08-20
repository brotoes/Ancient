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
public class Seller implements Matchable {
    private final int minPrice;
    /* Quantity available for purchase */
    private final int qty;
    private final Building building;
    private final String resource;

    private Buyer buyer = null;

    public Seller(Building b, int qty, int min, String resource) {
        this.building = b;
        this.qty = qty;
        this.minPrice = min;
        this.resource = resource;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    /**
     * Creates pawn and sends to buyer
     */
    public void sell() {

    }

    @Override
    public String toString() {
        return "Selling " + qty + " " + resource + " for " + minPrice;
    }

    @Override
    public int getCost(Matchable obj) {
        if (obj instanceof Buyer) {
            Buyer buyer = (Buyer)obj;
            return (int)buyer.getBuilding().getProvince().getDistance(getBuilding().getProvince());
        } else {
            System.err.println("Warning: Seller matched with non-buyer");
            return -1;
        }
    }

    @Override
    public void setMatch(Matchable match) {
        if (match instanceof Buyer) {
            buyer = (Buyer)match;
        } else {
            System.err.println("Warning: Buyer matched with non-Seller");
        }
    }

    /* getters and setters */
    public Building getBuilding() { return building; }
}
