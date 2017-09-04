/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.map;

import ancient.Main;
import appStates.PlayAppState;
import ancient.buildings.Building;
import ancient.buildings.BuildingFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.shader.VarType;
import com.jme3.util.BufferUtils;
import controllers.game.TurnListener;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import mapGeneration.Selectable;
import pathfinder.Pathable;
import pathfinder.Pathfinder;
import ancient.pawns.Pawn;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import mapGeneration.geometry.Shape;

/**
 *
 * @author brock
 */
public class Province implements Selectable, Pathable, TurnListener {
    private static int nextId = 0;

    /* Graphical vars*/
    private final Material faceMat;
    private final Material outlineMat;
    private final Geometry faceGeom;
    private final Geometry outlineGeom;
    private final Node pivot = new Node("pivot");
    private final Node facePivot;
    private final Node outlinePivot;
    private final Node modelPivot;
    private final TerrainType terrainType;
    private final PlayAppState playState;
    private final float LINE_WIDTH = 1.5f;
    private final ColorRGBA OUTLINE_COLOR = ColorRGBA.Black;
    private final ColorRGBA SELECT_COLOR = ColorRGBA.White;
    private final ColorRGBA PATH_COLOR = ColorRGBA.Red;
    private final float OUTLINE_OFFSET = 0.01f;
    private final Shape shape;

    /* Pathing vars */
    private ArrayList<Province> adjProvs = null;
    private Pathfinder<Province> pathfinder = null;

    /* Gameplay Vars */
    private final int id;
    private final ArrayList<Pawn> pawns = new ArrayList<>();
    private final ArrayList<Building> buildings = new ArrayList<>();
    private ProvinceLevel level = null;
    private ColorRGBA owner = ColorRGBA.White;

    /**
     * Generates new province
     *
     * @param elevation Elevation above/below sealevel. range from -1.0 to 1.0
     * @param temp Temperature of region, from -1.0 to 1.0
     * @param shape Shape of province
     */
    public Province(float elevation, float temp, Shape shape) {
        id = Province.nextId;
        Province.nextId ++;
        playState = Main.app.getPlayState();
        /* Set up province properties */
        terrainType = TerrainType.getTerrainType(elevation, temp);
        this.shape = shape;

        /* Define faces */

        faceGeom = new Geometry("faceMesh", shape.getFaceMesh());

        faceMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        faceMat.setColor("Diffuse", terrainType.getColor());
        faceMat.setParam("UseMaterialColors", VarType.Boolean, true);

        faceGeom.setMaterial(faceMat);
        faceGeom.setUserData("clickTarget", new Selectable[]{this});

        facePivot = new Node("facePivot");
        facePivot.attachChild(faceGeom);
        pivot.attachChild(facePivot);

        /* Define Outline */

        outlineGeom = new Geometry("Outline", shape.getOutlineMesh());

        outlineMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        outlineMat.setColor("Color", OUTLINE_COLOR);
        outlineMat.getAdditionalRenderState().setLineWidth(LINE_WIDTH);

        outlineGeom.setMaterial(outlineMat);
        outlinePivot = new Node("outlinePivot");
        outlinePivot.attachChild(outlineGeom);
        pivot.attachChild(outlinePivot);

        modelPivot = new Node("modelPivot");
        Random rand = new Random();
        modelPivot.rotate(0, 0, rand.nextFloat()*6.28f);
        pivot.attachChild(modelPivot);

        /* display everything */
        playState.getNode().attachChild(pivot);
        outlinePivot.setLocalTranslation(new Vector3f(0.0f, 0.0f, OUTLINE_OFFSET));
        pivot.setLocalTranslation(shape.getCenter().getVector());

        updateLevel();
    }

    /**
     * returns true if a buildingFactory is valid for this province
     * @param fac
     * @return
     */
    public boolean isValid(BuildingFactory fac) {
        /* test if already exists */
        for (int i = 0; i < getNumBuildings(); i ++) {
            Building b = getBuilding(i);
            if (b.getName().equals(fac.getName())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Populates neighbors. Should be called after all Provinces have been instantiated
     *
     */
    public void findNeighbors() {
        //adjProvs = voronoi.getNeighbors(polyInd);
        //TODO
        adjProvs = null;
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

    public void step() {

    }

    @Override
    public List<Province> getNeighbors() {
        if(adjProvs == null) {
            findNeighbors();
        }
        return Collections.unmodifiableList(adjProvs);
    }

    /**
     * returns path from prov to this
     * @param prov
     * @return
     */
    public List<Province> getPath(Province prov) {
        if (pathfinder == null) {
            pathfinder = new Pathfinder<>(this);
        }
        List<Province> path = pathfinder.getPath(prov);

        return Collections.unmodifiableList(path);
    }

    /**
     * returns geometry corresponding to passed in path
     *
     * @param path
     * @return
     */
    public Geometry getPathGeom(List<Province> path) {
        Material mat;
        Geometry geom;
        Mesh mesh = new Mesh();
        FloatBuffer buf = BufferUtils.createFloatBuffer(path.size()*3);

        for (Province i : path) {
            buf.put(i.shape.getCenter().getX());
            buf.put(i.shape.getCenter().getY());
            buf.put(i.shape.getCenter().getZ() + OUTLINE_OFFSET);
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

    /**
     * Adds a building to the province
     * @param building
     */
    public void addBuilding(Building building) {
        //TEMPORARY, remove
        setOwner(ColorRGBA.Red);

        buildings.add(building);
        updateLevel();
    }

    /**
     * Sets the province level according the building count
     */
    private void updateLevel() {
        modelPivot.detachAllChildren();
        level = ProvinceLevel.getLevel(getNumBuildings());
        if (level.getModel() != null) {
            modelPivot.attachChild(level.getModel());
        }
    }

    public void setOwner(ColorRGBA owner) {
        this.owner = owner;
        //Main.app.getPlayState().getWorldMap().updateBorders();
    }

    public ColorRGBA getOwner() { return owner; }

    /**
     * checks if any adjacent provinces don't share an owner
     * @return
     */
    public boolean isBorderProvince() {
        boolean border = false;
        for (Province i : getNeighbors()) {
            if (i.getOwner() != getOwner()) {
                return true;
            }
        }

        return border;
    }

    @Override
    public void nextTurn() {
        for (Building b : buildings) {
            b.produce();
        }
    }

    /* Getters and setters */
    public float getDistance(Province prov) {
        return pivot.getLocalTranslation().distance(
                prov.getPivot().getLocalTranslation());
    }

    public int getId() { return id; }
    public Building getBuilding(int index) { return buildings.get(index); }
    public int getNumBuildings() { return buildings.size(); }
    public int getNumPawns() { return pawns.size(); }
    public Pawn getPawn(int index) { return pawns.get(index); }
    public void removePawn(Pawn pawn) { pawns.remove(pawn); }
    public void addPawn(Pawn pawn) { pawns.add(pawn); }
    public Shape getShape() { return shape; }
}
