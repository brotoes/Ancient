/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration;

import fastnoise.FastNoise;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;
import mapGeneration.geometry.Shape;
import utils.MathUtils;

/**
 *
 * @author brock
 */
public class Voronoi {
    private final OpenList sites = new OpenList();
    private final List<RefSite> refSites = new ArrayList<>();
    /*
     * Manipulate these to change the sizes of the provinces based on their
     * elevations
     */
    private final double weightConstant = 1.0;
    private final double weightFactor = 25.0;

    /**
     *
     * @param width: Width of the Voronoi Diagram
     * @param height: Height of the Voronoi Diagram
     * @param nSites: Number of sites in the Voronoi Diagram
     * @param nPasses: Number of passes relaxing the voronoi diagram
     * @param seed: seed for site selection
     * @param weightMap
     */
    public Voronoi(int width, int  height, int nSites, int nPasses, int seed,
            FastNoise weightMap) {
        PowerDiagram pd = new PowerDiagram();
        PolygonSimple rootPolygon = new PolygonSimple();
        Random rand = new Random(seed);

        rootPolygon.add(0, 0);
        rootPolygon.add(width, 0);
        rootPolygon.add(width, height);
        rootPolygon.add(0, height);

        for (int i = 0; i < nSites; i ++) {
            Site site = new Site(rand.nextFloat()*width, rand.nextFloat()*height);
            double weight = weightMap.GetNoise((float)site.getX()/width,
                    (float)site.getY()/height);
            weight = MathUtils.map(weight, -1.0, 1.0, 1, 10);
            //site.setWeight(weight);
            sites.add(site);
        }

        pd.setClipPoly(rootPolygon);
        pd.setSites(sites);
        pd.computeDiagram();

        /* Compute Lloyd relaxation */
        for (int i = 0; i < nPasses; i ++) {
            for (int j = 0; j < nSites; j ++) {
                Site site = sites.get(j);
                sites.get(j).setX(site.getPolygon().getCentroid().x);
                sites.get(j).setY(site.getPolygon().getCentroid().y);
                double weight = weightMap.GetNoise((float)site.getX()/width,
                    (float)site.getY()/height);
                weight = MathUtils.map(weight, -1.0, 1.0, 1, 10);
                //site.setWeight(weight);
            }
            pd.computeDiagram();
        }

        /* populate provSites list */
        for (int i = 0; i < sites.size; i ++) {
            refSites.add(new RefSite(sites.get(i)));
            sites.get(i).setIndex(i);
        }
    }

    public int size() {
        return sites.size;
    }

    /**
     * returns polygon corresponding to index
     * @param index
     * @return
     */
    public PolygonSimple getPolygon(int index) {
        return sites.get(index).getPolygon();
    }

    /* links shape to site */
    public void setShape(int index, Shape shape) {
        refSites.get(index).setShape(shape);
    }

    /* returns list of provinces adjacent to site of given index */
    public List<Shape> getNeighbors(int index) {
        List<Site> adjSites = sites.get(index).getNeighbours();
        List<Shape> adjs = new ArrayList<>(adjSites.size());
        for (Site s : adjSites) {
            int i = s.getIndex();
            Shape p = refSites.get(i).getProvince();
            adjs.add(p);
        }

        return adjs;
    }
}
