/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration;

import kn.uni.voronoitreemap.j2d.Site;
import mapGeneration.geometry.Shape;

/**
 * Extension of Site that links to a province.
 *
 * @author brock
 */
public class RefSite {
    private Shape shape;
    private final Site site;

    public RefSite(Site site){
        this.site = site;
    }

    public Site getSite() {
        return site;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Shape getProvince() {
        return shape;
    }
}
