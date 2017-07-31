/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration;

import ancient.Main;
import appStates.PlayAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.shader.VarType;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import kn.uni.voronoitreemap.j2d.PolygonSimple;

/**
 *
 * @author brock
 */
public class Province implements Selectable {
    private final Vector3f center;
    private final Material faceMat;
    private final Material outlineMat;
    private final Mesh faceMesh;
    private final Mesh outlineMesh;
    private final Geometry faceGeom;
    private final Geometry outlineGeom;
    private final FloatBuffer buf;
    private final FloatBuffer outlineBuf;
    private final FloatBuffer normalBuf;
    private final Node pivot = new Node("pivot");
    private final SelectableNode facePivot;
    private final Node outlinePivot;
    private final TerrainType terrainType;
    private final PlayAppState playState;
    private final float LINE_WIDTH = 2.0f;
    private final ColorRGBA OUTLINE_COLOR = ColorRGBA.Black;
    private final ColorRGBA SELECT_COLOR = ColorRGBA.White;
    private final float OUTLINE_OFFSET = 0.1f;
    private final Voronoi voronoi;
    private final int polyInd;
    
    private ArrayList<Province> adjProvs;
    
    /**
     * Generates new province
     * 
     * @param elevation Elevation above/below sealevel. range from -1.0 to 1.0
     * @param temp Temperature of region, from -1.0 to 1.0
     * @param vor Voronoi diagram to get polygon from
     * @param polyInd Index of polygon in vor
     * @param zPoints
     * @param centerZ
     */
    public Province(float elevation, float temp, Voronoi vor, int polyInd, float[] zPoints, float centerZ) {
        playState = Main.app.getPlayState();
        /* Set up province properties */
        terrainType = TerrainType.getTerrainType(elevation, temp);
        this.voronoi = vor;
        this.polyInd = polyInd;

        /* Get data from voronoi */
        PolygonSimple polygon = vor.getPolygon(polyInd);
        vor.setProvince(polyInd, this);
        
        /* Set up geometry Buffers */
        centerZ = Math.max(0.0f, centerZ);
        center = new Vector3f((float)polygon.getCentroid().x, (float)polygon.getCentroid().y, centerZ);
        double[] vertsX = polygon.getXPoints();
        double[] vertsY = polygon.getYPoints();
        
        buf = BufferUtils.createFloatBuffer((polygon.getNumPoints() + 2)*3);
        normalBuf = BufferUtils.createFloatBuffer((polygon.getNumPoints() + 2)*3);
        outlineBuf = BufferUtils.createFloatBuffer((polygon.getNumPoints())*3);
        
        buf.put(0.0f);
        buf.put(0.0f);
        buf.put(0.0f);
        
        normalBuf.put(0.0f);
        normalBuf.put(0.0f);
        normalBuf.put(0.0f);
        
        Vector3f avgVec = new Vector3f(0,0,0);
        Vector3f firstVec = null;
        
        for (int i = 0; i < polygon.getNumPoints(); i ++) {
            int nextInd = (i + 1) % polygon.getNumPoints();
            Vector3f vector = new Vector3f((float)(vertsX[i] - center.x),
                (float)(vertsY[i] - center.y), zPoints[i] - centerZ);
            Vector3f nVec   = new Vector3f((float)(vertsX[nextInd] - center.x),
                (float)(vertsY[nextInd] - center.y), zPoints[nextInd] - centerZ);
            
            buf.put(vector.x);
            buf.put(vector.y);
            buf.put(vector.z);
            
            /* Calculate normal based on this and the next vector */
            Vector3f normal = vector.cross(nVec).normalize();
            if (firstVec == null) {
                firstVec = normal;
            }
            avgVec.addLocal(normal);
            
            normalBuf.put(normal.x);
            normalBuf.put(normal.y);
            normalBuf.put(normal.z);
            
            outlineBuf.put(vector.x);
            outlineBuf.put(vector.y);
            outlineBuf.put(vector.z);
        }
        avgVec.divideLocal(polygon.getNumPoints()).normalize();
        
        buf.put((float)(vertsX[0] - center.x));
        buf.put((float)(vertsY[0] - center.y));
        buf.put(zPoints[0] - centerZ);
        
        if (firstVec != null) {
            normalBuf.put(firstVec.x);
            normalBuf.put(firstVec.y);
            normalBuf.put(firstVec.z);
            
            normalBuf.put(0, firstVec.x);
            normalBuf.put(1, firstVec.y);
            normalBuf.put(2, firstVec.z);
        }
        
        normalBuf.put(0, avgVec.x);
        normalBuf.put(1, avgVec.y);
        normalBuf.put(2, avgVec.z);
        
        
        /* Define faces */
        faceMesh = new Mesh();
        faceMesh.setMode(Mesh.Mode.TriangleFan);
        faceMesh.setBuffer(VertexBuffer.Type.Position, 3, buf);
        faceMesh.setBuffer(VertexBuffer.Type.Normal, 3, normalBuf);
        faceGeom = new Geometry("prov-", faceMesh);

        faceMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        //faceMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        faceMat.setColor("Diffuse", terrainType.getColor());
        faceMat.setParam("UseMaterialColors", VarType.Boolean, true);
        
        faceGeom.setMaterial(faceMat);
        
        facePivot = new SelectableNode("facePivot", this);
        facePivot.attachChild(faceGeom);
        pivot.attachChild(facePivot);
        faceGeom.updateModelBound();
        
        /* Define Outline */
        outlineMesh = new Mesh();
        outlineMesh.setMode(Mesh.Mode.LineLoop);
        outlineMesh.setBuffer(VertexBuffer.Type.Position, 3, outlineBuf);
        outlineGeom = new Geometry("Outline", outlineMesh);
        
        outlineMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        outlineMat.setColor("Color", ColorRGBA.Black);
        outlineMat.getAdditionalRenderState().setLineWidth(LINE_WIDTH);
        
        outlineGeom.setMaterial(outlineMat);
        outlinePivot = new Node("outlinePivot");
        outlinePivot.attachChild(outlineGeom);
        pivot.attachChild(outlinePivot);
        
        /* display everything */
        playState.getNode().attachChild(pivot);
        outlinePivot.setLocalTranslation(new Vector3f(0.0f, 0.0f, OUTLINE_OFFSET));
        pivot.setLocalTranslation(center);
    }
    
    /**
     * Populates neighbors. Should be called after all Provinces have been instantiated
     * 
     */
    public void findNeighbors() {
        adjProvs = voronoi.getNeighbors(polyInd);
    }
    
    @Override
    public void select() {
        outlineMat.setColor("Color", SELECT_COLOR);
        outlinePivot.setLocalTranslation(new Vector3f(0.0f, 0.0f, OUTLINE_OFFSET*2));
    }
    
    @Override
    public void deselect() {
        outlineMat.setColor("Color", OUTLINE_COLOR);
        outlinePivot.setLocalTranslation(new Vector3f(0, 0, OUTLINE_OFFSET));
    }
    
    public Node getPivot() { return pivot; }
}
