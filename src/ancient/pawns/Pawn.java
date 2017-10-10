package ancient.pawns;


import ancient.Main;
import ancient.buildings.Building;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shader.VarType;
import controllers.game.TurnListener;
import ancient.map.Province;
import ancient.players.Player;
import ancient.resources.Resource;
import ancient.resources.ResourceContainer;
import controllers.gui.Infoable;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import java.util.List;
import mapGeneration.Selectable;
import utils.StrUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author brock
 */
public class Pawn implements Selectable, Infoable, TurnListener {
    private static int nextId = 0;

    private final int id;

    /* Graphical Vars */
    private final Node pivot;
    private final Geometry geom;
    private final Box box;
    private final Material mat;
    private final ColorRGBA color = ColorRGBA.Red;
    private final ColorRGBA selectedColor = ColorRGBA.White;
    private final ResourceContainer resourceContainer;

    /* GUI Vars */
    private Element infoPanel;
    private Element parentPanel;
    private Nifty nifty;
    private Screen screen;
    boolean showingInfoPanel = false;

    /* Game Vars */
    private Player owner;
    private List<Province> path = null;
    private Building destBuilding = null;
    private Province province;
    private Geometry pathGeom;

    public static Pawn newPawn(Province prov, Resource resource, int qty) {
        return Pawn.newPawn(prov, new ResourceContainer(resource, qty));
    }

    public static Pawn newPawn(Province prov, ResourceContainer resource) {
        Pawn pawn = new Pawn(prov, resource);
        Main.app.getPlayState().getTurnController().addListener(pawn);
        prov.addPawn(pawn);
        return pawn;
    }

    /**
     * Spawns a new pawn at province
     * @param province
     * @param resourceContainer
     */
    private Pawn(Province province, ResourceContainer resourceContainer) {
        this.owner = province.getOwner();
        this.resourceContainer = resourceContainer;
        this.id = Pawn.nextId;
        Pawn.nextId ++;
        this.province = province;
        pivot = new Node("pivot");

        box = new Box(0.4f,0.4f,0.4f);
        geom = new Geometry("geom", box);
        mat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", resourceContainer.getResource().getColor());
        mat.setParam("UseMaterialColors", VarType.Boolean, true);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0, 0, 0.5f);

        geom.setUserData("clickTarget", new Selectable[]{this});
        pivot.attachChild(geom);
        pivot.setLocalTranslation(province.getPivot().getLocalTranslation());
    }

    @Override
    public void select() {
        showPathGeom();
        mat.setColor("Diffuse", selectedColor);
    }

    @Override
    public void deselect() {
        hidePathGeom();
        mat.setColor("Diffuse", color);
    }

    @Override
    public void nextTurn() {
        move();
    }

    /**
     * Move to next province in path
     */
    public void move() {
        if (path == null) {
            return;
        }
        /* remove from current province and add to next province in path */
        path.get(0).removePawn(this);
        path.get(1).addPawn(this);
        Province preProv = province;
        province = path.get(1);
        path.remove(0);
        if (path.size() <= 1) {
            path = null;
        }

        if (pathGeom != null) {
            showPathGeom();
        }

        /* if arrived at destination building, put resources into building */
        if (destBuilding != null &&
                destBuilding.getProvince() == getProvince()) {
            destBuilding.addResource(resourceContainer);
            this.province.removePawn(this);
            this.province = null;
        }
    }

    public void showPathGeom() {
        if (pathGeom != null) {
            hidePathGeom();
        }
        if (path != null) {
            pathGeom = province.getPathGeom(path);
            Main.app.getPlayState().getTopNode().attachChild(pathGeom);
        }
    }

    public void hidePathGeom() {
        if (pathGeom != null) {
            Main.app.getPlayState().getTopNode().detachChild(pathGeom);
            pathGeom = null;
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
    public void refreshInfoPanel() {
        if (showingInfoPanel) {
            removeInfoPanel();
            showInfoPanel(nifty, screen, parentPanel);
        }
    }

    @Override
    public void infoClick(String... args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeInfoPanel() {
        showingInfoPanel = false;
        if (infoPanel != null) {
            infoPanel.markForRemoval();
            infoPanel = null;
        }
    }

    private PanelBuilder getOwnedInfoPanelBuilder() {
        Pawn that = this;
        return new PanelBuilder() {{
            childLayoutVertical();
            visibleToMouse();
            width("100%");
            padding("10px");
            backgroundColor("#444");
            /* Show pawn resource name and owner */
            panel(new PanelBuilder() {{
                childLayoutHorizontal();
                text(new TextBuilder() {{
                    text(that.getResource().getName());
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
        }};
    }

    private PanelBuilder getOtherInfoPanelBuilder() {
        Pawn that = this;
        return new PanelBuilder() {{
            childLayoutVertical();
            visibleToMouse();
            width("100%");
            padding("10px");
            backgroundColor("#444");
            /* Show pawn resource name and owner */
            panel(new PanelBuilder() {{
                childLayoutHorizontal();
                text(new TextBuilder() {{
                    text(that.getResource().getName());
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
        }};
    }

    /* Getters and setters */
    public Province getProvince() { return province; }
    public int getId() { return id; }
    public Player getOwner() { return owner; }
    public void setDestination(Province dest) { path = dest.getPath(province); }
    public void setDestination(Building building) {
        setDestination(building.getProvince());
        destBuilding = building;
    }
    public Province getDestination() { return path.get(path.size() - 1); }
    public ResourceContainer getResourceContainer() { return resourceContainer; }
    public Resource getResource() { return resourceContainer.getResource(); }
    public Node getPivot() { return pivot; }
}
