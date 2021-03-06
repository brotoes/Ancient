/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appStates;

import actionTracker.ActionTracker;
import ancient.Main;
import ancient.map.Province;
import ancient.map.TerrainType;
import ancient.pawns.Pawn;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import controllers.trade.TradeController;
import controllers.game.TurnController;
import controllers.interaction.CameraController;
import controllers.interaction.PlayInputController;
import java.util.ArrayList;
import ancient.map.WorldMap;
import ancient.players.Player;
import ancient.resources.Resource;
import controllers.mapModes.MapModeController;
import controllers.gui.HudController;
import controllers.network.messages.StartGameMessage;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.toList;
import network.messages.Message;

/**
 *
 * @author brock
 */
public class PlayState extends AppState {
    private TradeController tradeCon;
    private TurnController turnCon;
    private CameraController camCon;
    private WorldMap worldMap;
    private MapModeController mapModeCon;
    private final List<Pawn> pawns = new ArrayList<>();
    private final ActionTracker actionTracker = new ActionTracker();

    public PointLight camLight;

    /** spatials added to this node render after and without depth testing */
    private final Node topNode = new Node("topNode");
    /** when added to this node, spatials render normally */
    private final Node node = new Node("stateNode");

    public PlayState(WorldMap wm) {
        /* if null is passed, generated a WorldMap */
        this.worldMap = wm;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        /* Set GUI */
        Main.app.getNifty().gotoScreen("hud");

        /* Set up camera */
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);

        app.getCamera().setLocation(new Vector3f(50, 40, 25));
        app.getCamera().setFrustumFar(1000);

        /* Set up Lighting */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1.0f, 0.1f, -5.0f).normalize());
        sun.setColor(ColorRGBA.White);

        camLight = new PointLight();
        camLight.setRadius(100.0f);
        camLight.setColor(ColorRGBA.White);

        node.addLight(sun);
        node.addLight(camLight);

        /* set up world data */
        Resource.parseResources();
        TerrainType.load();

        /* set up the scene */
        mapModeCon = new MapModeController();
        turnCon = new TurnController();
        tradeCon = new TradeController();

        if (worldMap == null) {
            worldMap = new WorldMap(1000, 1337);
            worldMap.init();

            /* put players in their start positions */
            Main.app.getPlayerManager().getPlayers().stream().forEach(player -> {
                List<Province> starts = worldMap.getProvinces().stream().filter(p ->
                    p.getOwner() == null && !p.getTerrainType().getName().equals("Water"))
                    .collect(toList());

                Random rand = new Random();
                Province startProv = starts.get(rand.nextInt(starts.size()));
                startProv.setStartClaim(player);
            });

            Message msg = new StartGameMessage(worldMap);
            Main.app.getNetworkController().send(msg);
        } else {
            Province.updateProvs(worldMap.getProvinces());
            worldMap.init();
        }

        camCon = new CameraController(this);
        inputCon = new PlayInputController(this);

        /* Set all players to not ready */
        Main.app.getPlayerManager().getPlayers().stream().forEach(p -> p.setReady(false));

        enable();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        // unregister all listeners, detach nodes etc
        this.app.getRootNode().detachChild(node);
    }

    @Override
    protected void enable() {
        super.enable();
        rootNode.attachChild(node);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        camCon.tick(tpf);

        for (int i = 0; i < worldMap.getNumProvs(); i ++) {
            worldMap.getProvince(i).step();
        }

        for (Pawn i : pawns) {
            //i.update();
        }
    }

    @Override
    public void render(RenderManager rm) {
        rm.renderScene(topNode, viewPort);
    }

    /**
     * Adds pawns. TODO: REMOVE
     * @param pawn
     */
    public void addPawn(Pawn pawn) {
        pawns.add(pawn);
    }

    @Override
    public void updatePlayer(Player player) {
        //TODO: player change update
    }

    /* Getters and Setters */
    public Node getNode() { return node; }
    public Node getTopNode() { return topNode; }
    public CameraController getCameraController () { return camCon; }
    public TurnController getTurnController() { return turnCon; }
    public TradeController getTradeController() { return tradeCon; }
    public WorldMap getWorldMap() { return worldMap; }
    public MapModeController getMapModeController() { return mapModeCon; }
    public ActionTracker getActionTracker() { return actionTracker; }
    public HudController getHudController() {
        ScreenController con = Main.app.getScreenController();
        if (con instanceof HudController) {
            return (HudController)con;
        } else {
            System.err.println("Type Error: ScreenController not HudController");
            return null;
        }
    }
}
