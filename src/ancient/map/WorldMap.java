/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.map;

import ancient.Main;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import fastnoise.FastNoise;
import fastnoise.FastNoise.NoiseType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import mapGeneration.Voronoi;
import utils.ArrUtils;

/**
 *
 * @author brock
 */
public class WorldMap {
    private final int nProvs = 1000;
    private final Province[] provs = new Province[nProvs];
    private final int seed = 1337;
    private final int freq = 3;
    private final float amp = 2.0f;
    private final float zFactor = 8.0f;

    private final int width = 100;
    private final int height = 90;

    private final Node borderPivot = new Node("borderPivot");

    public WorldMap() {
        FastNoise elevationMap = new FastNoise(seed);
        elevationMap.SetFrequency(freq);

        TerrainType.load();
        elevationMap.SetFrequency(freq);
        elevationMap.SetNoiseType(NoiseType.SimplexFractal);

        Voronoi voronoi = new Voronoi(width, height, nProvs, 3, seed, elevationMap);

        /* Generate Each Province from voronoi */
        for (int i = 0; i < voronoi.size(); i ++) {
            PolygonSimple polygon = voronoi.getPolygon(i);
            Point2D cent = polygon.getCentroid();
            float elevation = elevationMap.GetNoise((float)cent.x/width, (float)cent.y/height);

            //elevation *= amp;
            /* Map temperature like a globe */
            float temperature = ((float)polygon.getCentroid().y/(float)height);
            temperature = -8*(float)Math.pow(temperature - 0.5f, 2.0f)+1;

            float[] zPoints = new float[polygon.getNumPoints()];
            float[] xPoints = ArrUtils.castToFloat(polygon.getXPoints());
            float[] yPoints = ArrUtils.castToFloat(polygon.getYPoints());

            /* find z values */
            for (int j = 0; j < zPoints.length; j ++) {
                zPoints[j] = Math.max(0.0f,
                        elevationMap.GetNoise(xPoints[j]/width, yPoints[j]/height))*zFactor;
            }

            /* Create province */
            provs[i] = new Province(elevation, temperature, voronoi, i, zPoints, Math.max(0.0f,elevation*zFactor));
            Main.app.getPlayState().getTurnController().addListener(provs[i]);
        }
        for (Province p : provs) {
            p.findNeighbors();
        }
    }

    /**
     * runs logic for each province to border itself in the correct color
     */
    public void createBorders() {
        borderPivot.detachAllChildren();
        /* Group provinces by ownership, adjacency group */
        HashMap<ColorRGBA, ArrayList<ArrayList<Province>>> groups = new HashMap<>();
        List<Province> added = new ArrayList<>();

        for (Province i : provs) {
            if (added.contains(i)) {
                continue;
            }
            Queue<Province> checkQueue = new ArrayDeque<>();
            checkQueue.add(i);
            added.add(i);
            while (!checkQueue.isEmpty()) {
                for (Province j : i.getNeighbors()) {

                }
            }
        }
    }

    /* Getters and setters */
    public Province getProvince(int index) {
        return provs[index];
    }

    public int getNumProvs() {
        return provs.length;
    }
}
