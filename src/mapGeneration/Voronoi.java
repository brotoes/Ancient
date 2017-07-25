/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration;

import java.util.Random;
import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

/**
 *
 * @author brock
 */
public class Voronoi {
    private final OpenList sites = new OpenList();
    
    /**
     * 
     * @param width: Width of the Voronoi Diagram
     * @param height: Height of the Voronoi Diagram
     * @param nSites: Number of sites in the Voronoi Diagram
     * @param nPasses: Number of passes relaxing the voronoi diagram
     * @param seed: seed for site selection
     */
    public Voronoi(int width, int  height, int nSites, int nPasses, int seed) {
        PowerDiagram pd = new PowerDiagram();
        PolygonSimple rootPolygon = new PolygonSimple();
        Random rand = new Random(seed);
        
        rootPolygon.add(0, 0);
        rootPolygon.add(width, 0);
        rootPolygon.add(width, height);
        rootPolygon.add(0, height);
    
        for (int i = 0; i < nSites; i ++) {
            sites.add(new Site(rand.nextFloat()*width, rand.nextFloat()*height));
        }
        
        pd.setClipPoly(rootPolygon);
        pd.setSites(sites);
        pd.computeDiagram();
        
        for (int i = 0; i < nPasses; i ++) {
            for (int j = 0; j < nSites; j ++) {
                sites.get(j).setX(sites.get(j).getPolygon().getCentroid().x);
                sites.get(j).setY(sites.get(j).getPolygon().getCentroid().y);
            }
            pd.computeDiagram();
        }
    }
    
    public int size() {
        return sites.size;
    }
    
    public PolygonSimple getPolygon(int index) {
        return sites.get(index).getPolygon();
    }
}
