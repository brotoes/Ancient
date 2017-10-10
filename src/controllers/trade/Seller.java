/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.trade;

import ancient.buildings.Building;
import hungarian.Matchable;
import ancient.pawns.Pawn;
import ancient.resources.Resource;
import ancient.resources.ResourceContainer;

/**
 *
 * @author brock
 */
public class Seller implements Matchable {
    private final Building building;
    private final ResourceContainer resourceContainer;

    private Buyer buyer = null;

    public Seller(Building b, ResourceContainer resourceContainer) {
        this.building = b;
        this.resourceContainer = resourceContainer;
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
        if (buyer != null) {
            Pawn pawn = Pawn.newPawn(building.getProvince(), resourceContainer);
            pawn.setDestination(buyer.getBuilding());
        }
    }

    @Override
    public String toString() {
        return "Selling " + resourceContainer;
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
    public Resource getResource() { return resourceContainer.getResource(); }
    public int getResourceQty() { return resourceContainer.getQty(); }
    public ResourceContainer getResourceContainer() { return resourceContainer; }
}
