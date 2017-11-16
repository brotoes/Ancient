/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.map;

import actionTracker.ActionTracker;
import actionTracker.BuildAction;
import ancient.Main;
import ancient.buildings.Building;
import ancient.buildings.BuildingFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import controllers.game.TurnListener;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import mapGeneration.Selectable;
import pathfinder.Pathable;
import pathfinder.Pathfinder;
import ancient.pawns.Pawn;
import ancient.players.Player;
import controllers.gui.Infoable;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import mapGeneration.geometry.Shape;
import utils.StrUtils;

/**
 *
 * @author brock
 */
public class Province implements Selectable, Infoable, Pathable, TurnListener {
    private static int nextId = 0;
    private static Map<Integer, Province> provs = new HashMap<>();

    /* static constants */
    private static final float LINE_WIDTH = 1.0f;
    private static final ColorRGBA OUTLINE_COLOR = ColorRGBA.Black;
    private static final ColorRGBA SELECT_COLOR = ColorRGBA.White;
    private static final ColorRGBA PATH_COLOR = ColorRGBA.Red;
    private static final float OUTLINE_OFFSET = 0.01f;

    /* Graphical vars*/
    private transient Material faceMat;
    private transient Material outlineMat;
    private transient Geometry faceGeom;
    private transient Geometry outlineGeom;
    private transient Node pivot;
    private transient Node facePivot;
    private transient Node outlinePivot;
    private transient Node modelPivot;
    private transient Node pawnPivot;
    private transient Node flagPivot;
    private Shape shape;

    /* Pathing vars */
    private transient Pathfinder<Province> pathfinder;

    /* Gameplay Vars */
    private int id;
    private String terrainTypeName;
    private final List<Pawn> pawns = new ArrayList<>();
    private final List<Building> buildings = new ArrayList<>();
    private final List<Player> claimants = new ArrayList<>();
    private final List<Flag> claimFlags = new ArrayList<>();
    private int owner = -1;
    private transient ProvinceLevel level;
    private transient Element infoPanel;
    private transient Element parentPanel;
    private transient Nifty nifty;
    private transient Screen screen;
    private transient boolean showingInfoPanel = false;

    /**
     * Generates new province
     *
     * @param elevation Elevation above/below sealevel. range from -1.0 to 1.0
     * @param temp Temperature of region, from -1.0 to 1.0
     * @param shape Shape of province
     */
    private Province(float elevation, float temp, Shape shape) {
        id = Province.nextId();
        /* Set up province properties */
        terrainTypeName = TerrainType.getTerrainType(elevation, temp).getName();
        this.shape = shape;
    }

    /**
     * No-arg constructor for use by Kryo Serializer
     */
    private Province() {}

    /**
     * constructor things to be done after the constructor. such as initializing
     * transient variables.
     */
    public void init() {
        /* Define faces */
        //TODO: width and height should be retreived from texture
        faceGeom = new Geometry("faceMesh", shape.getFaceMesh(100, 100));

        setFaceMaterial(Main.app.getPlayState().getMapModeController().getActiveMapMode().getProvinceMaterial(this));

        faceGeom.setUserData("clickTarget", new Selectable[]{this});

        facePivot = new Node("facePivot");
        facePivot.attachChild(faceGeom);
        pivot = new Node("pivot");
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
        pawnPivot = new Node("pawnPivot");
        flagPivot = new Node("flagPivot");
        Random rand = new Random();
        modelPivot.rotate(0, 0, rand.nextFloat()*6.28f);
        pivot.attachChild(modelPivot);
        pivot.attachChild(pawnPivot);
        pivot.attachChild(flagPivot);

        /* display everything */
        Main.app.getPlayState().getNode().attachChild(pivot);
        outlinePivot.setLocalTranslation(new Vector3f(0.0f, 0.0f, OUTLINE_OFFSET));
        modelPivot.setLocalTranslation(shape.getCenter().getVector());
        pawnPivot.setLocalTranslation(shape.getCenter().getVector());
        flagPivot.setLocalTranslation(shape.getCenter().getVector());

        updateLevel();
        Main.app.getPlayState().getTurnController().addListener(this);
    }

    public void setFaceMaterial(Material mat) {
        faceMat = mat;
        faceGeom.setMaterial(faceMat);
    }

    /**
     * returns true if a buildingFactory is valid for this province
     * @param fac
     * @return
     */
    public boolean isValid(BuildingFactory fac) {
        /* test if terrain is valid */
        if (!fac.getTerrain().contains(getTerrainType())) {
            return false;
        }

        /* test if already exists */
        for (int i = 0; i < getNumBuildings(); i ++) {
            Building b = getBuilding(i);
            if (b.getName().equals(fac.getName())) {
                return false;
            }
        }

        return true;
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

    public void step() {}

    /**
     * returns path from prov to this
     * @param prov
     * @return
     */
    public List<Province> getPath(Province prov) {
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
     * Sets the province level according the building count
     */
    private void updateLevel() {
        modelPivot.detachAllChildren();
        level = ProvinceLevel.getLevel(getNumBuildings());
        if (level.getModel() != null) {
            modelPivot.attachChild(level.getModel());
        }
    }


    /**
     * called when a player is claiming this province
     * @param player
     */
    public void claim(Player player) {
        /* Add player to claimants */
        claimants.add(player);
        Flag flag = new Flag(this, player);
        claimFlags.add(flag);
        flagPivot.attachChild(flag.getPivot());
        /* Oldest claim gets the province */
        //TODO: This code should be run at end of turn
        /*if (claimants.get(0) != getOwner()) {
            setOwner(claimants.get(0));
        }*/
        refreshInfoPanel();
    }
    public void claim(int id) {
        claim(Main.app.getPlayerManager().getPlayer(id));
    }
    public void claim(String id) {
        claim(Integer.valueOf(id));
    }
    /**
     * Removes a players claim from a province
     * @param player
     */
    public void releaseClaim(Player player) {
        claimants.remove(player);
        Flag flag = claimFlags.stream().filter(f -> f.getPlayer().equals(player)).findAny().get();
        flagPivot.detachChild(flag.getPivot());
        claimFlags.remove(flag);
        claimFlags.stream().forEach(f -> f.updatePosition());
        refreshInfoPanel();
    }
    public void releaseClaim(int id) {
        releaseClaim(Main.app.getPlayerManager().getPlayer(id));
    }
    public void releaseClaim(String id) {
        releaseClaim(Integer.valueOf(id));
    }

    /**
     * called to have a player start here
     * @param player
     */
    public void setStartClaim(Player player) {
        claimants.add(player);
        setOwner(player);
    }

    /**
     * builds a building and updates the province
     * @param fac
     */
    public void build(BuildingFactory fac) {
        Building b = fac.getBuilding(this);
        buildings.add(b);
        updateLevel();
        refreshInfoPanel();
    }
    public void build(int id) {
        build(BuildingFactory.getBuildingFactory(id));
    }
    public void build(String id) {
        build(Integer.valueOf(id));
    }

    /**
     * changes the owner of this province
     * @param owner
     */
    private void setOwner(Player owner) {
        if (owner != null) {
            this.owner = owner.getId();
        } else {
            this.owner = -1;
        }
        setFaceMaterial(Main.app.getPlayState().getMapModeController().getActiveMapMode().getProvinceMaterial(this));
        Main.app.getPlayState().getWorldMap().updateBorders();
    }

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

    @Override
    public Element showInfoPanel(Nifty nifty, Screen screen, Element elem) {
        /* If already built, return existing panel */
        if (infoPanel != null) {
            return infoPanel;
        }

        PanelBuilder pb;
        /* Panel for Province owned by the player */
        if (getOwner() != null && getOwner().isLocal()) {
            pb = getOwnedInfoPanelBuilder();
        /* Panel for unowned Province */
        } else {
            pb = getOtherInfoPanelBuilder();
        }

        infoPanel = pb.build(nifty, screen, elem);

        parentPanel = elem;
        this.nifty = nifty;
        this.screen = screen;
        showingInfoPanel = true;

        return infoPanel;
    }

    @Override
    public void infoClick(String... args) {
        ActionTracker at = Main.app.getPlayState().getActionTracker();
        String method = args[0];
        switch (method) {
            case "claim":
                claim(args[1]);
                break;
            case "releaseClaim":
                releaseClaim(args[1]);
                break;
            case "build":
                at.act(new BuildAction(this.getId(), Integer.valueOf(args[1])));
                break;
            default:
                System.err.println("Error: Invalid infoClick Method");
        }
    }

    @Override
    public void removeInfoPanel() {
        showingInfoPanel = false;
        if (infoPanel != null) {
            infoPanel.markForRemoval();
            infoPanel = null;
        }
    }

    @Override
    public void refreshInfoPanel() {
        if (showingInfoPanel) {
            removeInfoPanel();
            showInfoPanel(nifty, screen, parentPanel);
        }
    }

    /**
     * returns a panelBuilder for the infoPanel assuming this province is
     * owned by the player
     * @return
     */
    private PanelBuilder getOwnedInfoPanelBuilder() {
        Province that = this;
        return new PanelBuilder() {{
            childLayoutVertical();
            visibleToMouse();
            width("100%");
            padding("10px");
            backgroundColor("#444");
            /* Show province name and owner */
            panel(new PanelBuilder() {{
                childLayoutHorizontal();
                text(new TextBuilder() {{
                    text(that.getName());
                    style("text-style");
                }});
                text(new TextBuilder() {{
                    text("-");
                    style("text-style");
                    width("*");
                }});
                text(new TextBuilder() {{
                    text(that.getOwner().getName());
                    color(StrUtils.hexString(that.getOwner().getColor()));
                    style("text-style");
                }});
            }});
            /* Show province level and terrain type */
            panel(new PanelBuilder() {{
                childLayoutHorizontal();
                text(new TextBuilder() {{
                    text(that.getTerrainType().getName());
                    style("text-style");
                }});
                text(new TextBuilder() {{
                    text("-");
                    style("text-style");
                    width("*");
                }});
                text(new TextBuilder() {{
                    text(that.getLevel().getName());
                    style("text-style");
                }});
            }});
            /* Show Claimants */
            text(new TextBuilder() {{
                text("Claimants:");
                style("text-style");
            }});
            that.claimants.stream().forEach(c -> {
                text(new TextBuilder() {{
                    text(c.getName());
                    color(StrUtils.hexString(c.getColor()));
                    style("text-style");
                }});
            });
            /* Show Built Buildings */
            text(new TextBuilder() {{
                text("Buildings: ");
                style("text-style");
            }});
            panel(new PanelBuilder() {{
                childLayoutVertical();
                backgroundColor("#666");
                width("100%");
                padding("10px");
                if (that.getNumBuildings() == 0) {
                    text(new TextBuilder() {{
                        text("Nothing Built");
                        style("text-style");
                    }});
                }
                that.getBuildings().forEach(b -> {
                    text(new TextBuilder() {{
                        text(b.getName());
                        style("text-style");
                    }});
                });
            }});
            /* Show Available Buildings */
            final List<BuildingFactory> facs = BuildingFactory.getValidBuildingFactories(that);
            text(new TextBuilder() {{
                text("Available Buildings: ");
                style("text-style");
            }});
            panel(new PanelBuilder() {{
                childLayoutVertical();
                backgroundColor("#666");
                width("100%");
                padding("10px");
                if (facs.isEmpty()) {
                    text(new TextBuilder() {{
                        text("Nothing to Build");
                        style("text-style");
                    }});
                }
                facs.forEach(b -> {
                    control(new ControlBuilder(b.getName() + "_button", "button") {{
                        parameter("label", b.getName());
                        interactOnClick("infoClick(build|" + b.getId() + ")");
                    }});
                });
            }});
        }};
    }


    /**
     * returns a panelbuilder for when this province is not owned by this player
     * @return
     */
    private PanelBuilder getOtherInfoPanelBuilder() {
        Province that = this;
        Player player = Main.app.getPlayerManager().getLocalPlayer();
        //TODO: test if next to a province owned by player and not claimed this turn
        boolean claimable = isClaimable();
        return new PanelBuilder() {{
            childLayoutVertical();
            visibleToMouse();
            width("100%");
            padding("10px");
            backgroundColor("#444");
            /* Show province name */
            text(new TextBuilder() {{
                text(that.getName());
                style("text-style");
            }});

            /* Show province level and terrain type */
            panel(new PanelBuilder() {{
                childLayoutHorizontal();
                text(new TextBuilder() {{
                    text(that.getTerrainType().getName());
                    style("text-style");
                }});
                text(new TextBuilder() {{
                    text("-");
                    style("text-style");
                    width("*");
                }});
                text(new TextBuilder() {{
                    text(that.getLevel().getName());
                    style("text-style");
                }});
            }});

            /* Show centered claim button */
            panel(new PanelBuilder() {{
                childLayoutHorizontal();
                text(new TextBuilder() {{ width("*"); }});

                if (isClaimed()) {
                    control(new ControlBuilder("releaseclaim_button", "button") {{
                        parameter("label", "Release Claim");
                        interactOnClick("infoClick(releaseClaim|" + player.getId() + ")");
                    }});
                } else if (claimable) {
                    control(new ControlBuilder("claim_button", "button") {{
                        parameter("label", "Claim");
                        interactOnClick("infoClick(claim|" + player.getId() + ")");
                    }});
                } else {
                    text(new TextBuilder() {{
                        text("Unclaimable");
                        style("text-style");
                    }});
                }

                text(new TextBuilder() {{ width("*"); }});
            }});

            /* Show Claimants */
            if (getTerrainType().isClaimable()) {
                text(new TextBuilder() {{
                    text("Claimants:");
                    style("text-style");
                }});
                that.claimants.stream().forEach(c -> {
                    text(new TextBuilder() {{
                        text(c.getName());
                        color(StrUtils.hexString(c.getColor()));
                        style("text-style");
                    }});
                });
            }
        }};
    }

    /* Getters and setters */
    public float getDistance(Province prov) {
        return pivot.getLocalTranslation().distance(
                prov.getPivot().getLocalTranslation());
    }

    public int getId() { return id; }
    public String getName() { return "Province " + getId(); }
    public List<Building> getBuildings() {
        return Collections.unmodifiableList(buildings);
    }
    public Building getBuilding(int index) { return buildings.get(index); }
    public int getNumBuildings() { return buildings.size(); }
    public int getNumPawns() { return pawns.size(); }
    public Pawn getPawn(int index) { return pawns.get(index); }
    public void removePawn(Pawn pawn) {
        pawns.remove(pawn);
        pawnPivot.detachChild(pawn.getPivot());
    }
    public void addPawn(Pawn pawn) {
        pawns.add(pawn);
        pawnPivot.attachChild(pawn.getPivot());
    }
    public Player getOwner() {
        if (owner < 0) {
            return null;
        } else {
            return Main.app.getPlayerManager().getPlayer(owner);
        }
    }
    public List<Player> getClaimants() { return Collections.unmodifiableList(claimants); }
    public TerrainType getTerrainType() { return TerrainType.getTerrainType(terrainTypeName); }
    public ProvinceLevel getLevel() { return level; }
    public Shape getShape() { return shape; }
    @Override
    public String toString() { return "Province " + id; }
    @Override
    public List<Province> getNeighbors() {
        List<Province> neighbors = new ArrayList<>();
        List<Shape> adjs = shape.getAdjShapes();
        adjs.stream().forEach(a -> {
            neighbors.add(a.getProvince());
        });
        return neighbors;
    }

    /**
     * returns true if the local player has claimed this province.
     * @return
     */
    public boolean isClaimed() {
        return getClaimants().stream().anyMatch(p -> p.isLocal());
    }

    /**
     * returns if this province is claimable. returns true if both terrain is
     * claimable and there is an adjacent owned province
     * @return
     */
    public boolean isClaimable() {
        return !isClaimed() && getTerrainType().isClaimable() &&
                getNeighbors().stream().anyMatch(p -> {
                    try {
                        return p.getOwner().isLocal();
                    } catch (NullPointerException e) {
                        return false;
                    }
                });
    }

    /* static */
    /**
     * Creates and returns a new province and stores in provs map
     * @param elev
     * @param temp
     * @param shape
     * @return
     */
    public static Province newProvince(float elev, float temp, Shape shape) {
        Province prov = new Province(elev, temp, shape);
        provs.put(prov.getId(), prov);

        return prov;
    }

    /**
     * stores a collection of provinces by their IDs
     * @param newProvs
     */
    public static void updateProvs(Collection<Province> newProvs) {
        newProvs.stream().forEach(p -> provs.put(p.getId(), p));
    }

    /**
     * returns true if all provinces in a list share an owner
     * @param provs
     * @return
     */
    public static boolean sameOwner(List<Province> provs) {
        for (int i = 1; i < provs.size(); i ++) {
            if (provs.get(i).getOwner() != provs.get(i - 1).getOwner()) {
                return false;
            }
        }

        return true;
    }

    /**
     * returns the next id for a province and increments
     * @return
     */
    public static int nextId() {
        int id = nextId;
        nextId ++;
        return id;
    }

    /**
     * gets province by ID
     * @param id
     * @return
     */
    public static Province get(int id) {
        return provs.get(id);
    }
}
