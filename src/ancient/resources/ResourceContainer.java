/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.resources;

import java.util.List;

/**
 *
 * @author brock
 */
public class ResourceContainer {
    private final Resource resource;
    private int qty;

    public ResourceContainer(Resource resource, int qty) {
        this.qty = qty;
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "[" + resource + ", " + qty + "]";
    }

    @Override
    public ResourceContainer clone() {
        return new ResourceContainer(resource, qty);
    }

    /**
     * adds a resourcecontainers to a list of resourcecontainers.
     * verifies that a resource is not already in the list, and appends.
     * if already contained, increases quantity of existing container.
     *
     * @param newRC
     * @param list
     */
    public static void addToList(ResourceContainer newRC, List<ResourceContainer> list) {
        for (ResourceContainer rc : list) {
            if (rc.getResource() == newRC.getResource()) {
                rc.setQty(rc.getQty() + newRC.getQty());
                return;
            }
        }
        list.add(newRC.clone());
    }

    /**
     * removes a resource from a list of resourceContainers.
     * returns true if successfully removed, returns false if could not remove
     *
     * Assumes all of the resource is contained in a single resourceContainer
     *
     * @param remRC
     * @param list
     * @return
     */
    public static boolean removeFromList(ResourceContainer remRC, List<ResourceContainer> list) {
        for (ResourceContainer rc : list) {
            if (rc.getResource() == remRC.getResource()) {
                if (rc.getQty() >= remRC.getQty()) {
                    rc.setQty(rc.getQty() - remRC.getQty());
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    /* getters and setters */
    public Resource getResource() { return resource; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}
