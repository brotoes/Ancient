/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.resources;

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

    /* getters and setters */
    public Resource getResource() { return resource; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}
