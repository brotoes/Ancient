/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.trade;

import ancient.buildings.Building;
import ancient.resources.Resource;
import ancient.resources.ResourceContainer;
import hungarian.Matchable;

/**
 *
 * @author brock
 */
public class Buyer implements Matchable {
    private final Building building;
    private final ResourceContainer resourceContainer;

    private Seller seller = null;

    public Buyer(Building b, ResourceContainer resourceContainer) {
        this.building = b;
        this.resourceContainer = resourceContainer;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "Buying " + resourceContainer;
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
    public Resource getResource() { return resourceContainer.getResource(); }
    public int getQty() { return resourceContainer.getQty(); }
    public ResourceContainer getResourceContainer() { return resourceContainer; }
}
