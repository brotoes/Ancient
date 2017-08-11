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
import pathfinder.Pathable;
import pathfinder.Pathfinder;
import pawns.Pawn;

/**
 *
 * @author brock
 */
public class Province implements Selectable, Pathable {
    private final Vector3f center;
    private final Material faceMat;
    private final Material outlineMat;
    private final Mesh faceMesh;
    private final Mesh outlineMesh;
    private final Geometry faceGeom;
    private final Geometry outlineGeom;
    private final FloatBuffer vertexBuf;
    private final FloatBuffer outlineBuf;
    private final FloatBuffer normalBuf;
    private final Node pivot = new Node("pivot");
    private final SelectableNode facePivot;
    private final Node outlinePivot;
    private final TerrainType terrainType;
    private final PlayAppState playState;
    private final float LINE_WIDTH = 1.5f;
    private final ColorRGBA OUTLINE_COLOR = ColorRGBA.Black;
    private final ColorRGBA SELECT_COLOR = ColorRGBA.White;
    private final ColorRGBA PATH_COLOR = ColorRGBA.Red;
    private final float OUTLINE_OFFSET = 0.01f;
    private final Voronoi voronoi;
    private final int polyInd;

    private ArrayList<Province> adjProvs = null;
    private Pathfinder<Province> pathfinder = null;
    
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
        
        vertexBuf = BufferUtils.createFloatBuffer((polygon.getNumPoints() + 2)*3);
        normalBuf = BufferUtils.createFloatBuffer((polygon.getNumPoints() + 2)*3);
        outlineBuf = BufferUtils.createFloatBuffer(polygon.getNumPoints()*3);
        
        vertexBuf.put(0.0f);
        vertexBuf.put(0.0f);
        vertexBuf.put(0.0f);
        
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
            
            vertexBuf.put(vector.x);
            vertexBuf.put(vector.y);
            vertexBuf.put(vector.z);
            
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
        
        vertexBuf.put((float)(vertsX[0] - center.x));
        vertexBuf.put((float)(vertsY[0] - center.y));
        vertexBuf.put(zPoints[0] - centerZ);
        
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
        faceMesh.setBuffer(VertexBuffer.Type.Position, 3, vertexBuf);
        faceMesh.setBuffer(VertexBuffer.Type.Normal, 3, normalBuf);
        faceMesh.updateBound();
        faceGeom = new Geometry("faceMesh", faceMesh);

        faceMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        faceMat.setColor("Diffuse", terrainType.getColor());
        faceMat.setParam("UseMaterialColors", VarType.Boolean, true);
        
        faceGeom.setMaterial(faceMat);
        
        facePivot = new SelectableNode("facePivot", this);
        facePivot.attachChild(faceGeom);
        pivot.attachChild(facePivot);
        
        /* Define Outline */
        outlineMesh = new Mesh();
        outlineMesh.setMode(Mesh.Mode.LineLoop);
        outlineMesh.setBuffer(VertexBuffer.Type.Position, 3, outlineBuf);
        outlineMesh.updateBound();
        outlineGeom = new Geometry("Outline", outlineMesh);
        
        outlineMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        outlineMat.setColor("Color", OUTLINE_COLOR);
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
        
        //TODO REMOVE
        Main.app.getPlayState().addPawn(new Pawn(this));
    }
    
    @Override
    public void deselect() {
        outlineMat.setColor("Color", OUTLINE_COLOR);
        outlinePivot.setLocalTranslation(new Vector3f(0, 0, OUTLINE_OFFSET));
    }
    
    public Node getPivot() { return pivot; }
    
    public void step() {

    }

    @Override
    public ArrayList getNeighbors() {
        if(adjProvs == null) {
            findNeighbors();
        }
        return adjProvs;
    }
    
    /**
     * returns path from prov to this
     * @param prov
     * @return 
     */
    public ArrayList<Province> getPath(Province prov) {
        if (pathfinder == null) {
            pathfinder = new Pathfinder<>(this);
        }
        return pathfinder.getPath(prov);
    }
    
    /**
     * returns geometry corresponding to passed in path
     * 
     * @param path
     * @return 
     */
    public Geometry getPathGeom(ArrayList<Province> path) {
        Material mat;
        Geometry geom;
        Mesh mesh = new Mesh();
        FloatBuffer buf = BufferUtils.createFloatBuffer(path.size()*3);
        
        for (Province i : path) {
            buf.put(i.center.x);
            buf.put(i.center.y);
            buf.put(i.center.z + OUTLINE_OFFSET);
        }
        
        mesh.setMode(Mesh.Mode.LineStrip);
        mesh.setBuffer(VertexBuffer.Type.Position, 3, buf);
        mesh.updateBound();
        
        geom = new Geometry("PathGeom", mesh);
        
        mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", PATH_COLOR);
        mat.getAdditionalRenderState().setLineWidth(LINE_WIDTH);
        mat.getAdditionalRenderState().setDepthTest(false);
        geom.setMaterial(mat);
        
        return geom;
    }
    
    /**
     * returns geometry corresponding to path from start node
     * @param start
     * @return 
     */
    public Geometry getPathGeom(Province start) {
        return getPathGeom(getPath(start));
    }
}
