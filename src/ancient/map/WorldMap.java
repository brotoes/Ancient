/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.map;

import ancient.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import fastnoise.FastNoise;
import fastnoise.FastNoise.NoiseType;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import mapGeneration.Voronoi;
import mapGeneration.geometry.ProvVertex;
import mapGeneration.geometry.Shape;
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

        Main.app.getPlayState().getNode().attachChild(borderPivot);
        borderPivot.setLocalTranslation(new Vector3f(0, 0, 0.1f));

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
            Shape shape = new Shape(voronoi, i, zPoints, Math.max(0.0f,elevation*zFactor));
            voronoi.setShape(i, shape);
            provs[i] = new Province(elevation, temperature, shape);
            shape.setProvince(provs[i]);
            Main.app.getPlayState().getTurnController().addListener(provs[i]);
        }
        for (Province p : provs) {
            p.findNeighbors();
        }
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
            //group.add(i);
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
        List<Shape> shapes = new ArrayList<>();
        for (Province prov : provs) {
            shapes.add(prov.getShape());
        }
        List<ProvVertex> unsortedVerts = Shape.getBorderVerts(shapes);
        List<List<ProvVertex>> vertLists = ProvVertex.sortConnectedVertLoop(unsortedVerts);

        for (List<ProvVertex> verts : vertLists) {
            FloatBuffer buf = BufferUtils.createFloatBuffer(verts.size()*3);
            for (ProvVertex vert : verts) {
                buf.put(vert.getX());
                buf.put(vert.getY());
                buf.put(vert.getZ());
            }
            Mesh mesh = new Mesh();
            mesh.setMode(Mesh.Mode.LineLoop);
            mesh.setBuffer(VertexBuffer.Type.Position, 3, buf);
            mesh.updateBound();

            Geometry geom = new Geometry("Border", mesh);

            Material mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", provs.get(0).getOwner());
            mat.getAdditionalRenderState().setLineWidth(4.0f);

            geom.setMaterial(mat);

            if (provs.get(0).getOwner() != ColorRGBA.Red) {
                return;
            }

            borderPivot.attachChild(geom);
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
