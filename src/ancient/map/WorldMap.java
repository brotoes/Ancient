/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.map;

import ancient.Main;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import fastnoise.FastNoise;
import fastnoise.FastNoise.NoiseType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import mapGeneration.Voronoi;
import mapGeneration.geometry.ProvVertex;
import mapGeneration.geometry.Shape;
import utils.ArrUtils;
import utils.GeomUtils;

/**
 *
 * @author brock
 */
public class WorldMap implements Serializable {
    /* Map Generation Constants */
    private final static float ZFAC = 8.0f;
    private final static int FREQ = 3;
    //private final float amp = 2.0f;
    private final static int WIDTH = 100;
    private final static int HEIGHT = 90;

    private List<Province> provs;
    private FastNoise elevationMap;

    private transient Node borderPivot;

    public WorldMap(int nProvs, int seed) {
        this.provs = new ObjectArrayList<>(nProvs);
        elevationMap = new FastNoise(seed);
        elevationMap.SetFrequency(FREQ);

        elevationMap.SetFrequency(FREQ);
        elevationMap.SetNoiseType(NoiseType.SimplexFractal);

        Voronoi voronoi = new Voronoi(WIDTH, HEIGHT, nProvs, 3, seed, elevationMap);
        List<Shape> shapes = new ArrayList<>(nProvs);

        /* Generate Each Province from voronoi */
        for (int i = 0; i < voronoi.size(); i ++) {
            PolygonSimple polygon = voronoi.getPolygon(i);
            Point2D cent = polygon.getCentroid();
            float elevation = elevationMap.GetNoise((float)cent.x/WIDTH, (float)cent.y/HEIGHT);

            //elevation *= amp;
            /* Map temperature like a globe */
            float temperature = ((float)polygon.getCentroid().y/(float)HEIGHT);
            temperature = -8*(float)Math.pow(temperature - 0.5f, 2.0f)+1;

            float[] zPoints = new float[polygon.getNumPoints()];
            float[] xPoints = ArrUtils.castToFloat(polygon.getXPoints());
            float[] yPoints = ArrUtils.castToFloat(polygon.getYPoints());

            /* find z values */
            for (int j = 0; j < zPoints.length; j ++) {
                zPoints[j] = Math.max(0.0f,
                        elevationMap.GetNoise(xPoints[j]/WIDTH, yPoints[j]/HEIGHT))*ZFAC;
            }

            /* Create province */
            Shape shape = new Shape(voronoi, i, zPoints, Math.max(0.0f,elevation*ZFAC));
            shapes.add(shape);
            voronoi.setShape(i, shape);
            Province prov = new Province(elevation, temperature, shape);
            shape.getVertices().stream().forEach(v -> v.addProvince(prov));
            provs.add(prov);
            shape.setProvince(prov);
        }
        for (int i = 0; i < shapes.size(); i ++) {
            shapes.get(i).initNeighbors(voronoi, i);
        }
    }

    /**
     * Empty constructor for Kryo serializer
     */
    private WorldMap() {}

    /**
     * attaches everything to the outside world. separated from constructor
     * for serializing
     */
    public void init() {
        borderPivot = new Node("borderPivot");
        Main.app.getPlayState().getNode().attachChild(borderPivot);
        borderPivot.setLocalTranslation(new Vector3f(0, 0, 0.1f));
        provs.stream().forEach(p -> p.init());
        updateBorders();
    }

    /**
     * runs logic for each province to border itself in the correct color
     */
    public void updateBorders() {
        borderPivot.detachAllChildren();
        /* Group provinces by ownership, adjacency group */
        List<List<Province>> groups = new ArrayList<>();
        List<Province> added = new ArrayList<>();

        for (Province i : provs) {
            if (added.contains(i)) {
                continue;
            }
            List<Province> group = new ArrayList<>();
            Queue<Province> checkQueue = new ArrayDeque<>();
            checkQueue.add(i);
            added.add(i);
            groups.add(group);

            /* search all adjacent provinces until add adjacents added to group */
            while (!checkQueue.isEmpty()) {
                Province nextProv = checkQueue.remove();
                for (Province j : nextProv.getNeighbors()) {
                    if (!added.contains(j) && j.getOwner() == nextProv.getOwner()) {
                        checkQueue.add(j);
                        added.add(j);
                    }
                }
                group.add(nextProv);
            }
        }

        /* remove non-border provinces from each group. we only need to draw
         * the outside border provinces
         */
        for (List<Province> group : groups) {
            drawBorder(group);
        }
    }

    /**
     * Takes a group of adjacent provinces and draws the border
     * @param provs
     * @param pivot
     */
    private void drawBorder(List<Province> provs) {
        /* Only draw borders when the owner is not null/has a color*/
        if (provs.get(0).getOwner() == null) {
            return;
        }
        List<Shape> shapes = new ArrayList<>();
        for (Province prov : provs) {
            shapes.add(prov.getShape());
        }
        List<ProvVertex> unsortedVerts = Shape.getBorderVerts(shapes);
        List<List<ProvVertex>> vertLists = ProvVertex.sortConnectedVertLoop(unsortedVerts);

        for (List<ProvVertex> verts : vertLists) {
            Mesh mesh = GeomUtils.getBorderMesh(verts, 0.2, this);

            Geometry geom = new Geometry("Border", mesh);

            Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", provs.get(0).getOwner().getColor());
            mat.getAdditionalRenderState().setLineWidth(4.0f);

            geom.setMaterial(mat);

            borderPivot.attachChild(geom);
        }
    }

    /* Getters and setters */
    public Province getProvince(int index) { return provs.get(index); }
    public int getNumProvs() { return provs.size(); }
    public List<Province> getProvinces() { return Collections.unmodifiableList(provs); }

    public float getPointZ(float x, float y) {
        float elevation = elevationMap.GetNoise(x/WIDTH, y/HEIGHT);
        return Math.max(0.0f, elevation*ZFAC);
    }
}
