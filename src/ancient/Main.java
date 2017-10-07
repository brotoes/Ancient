package ancient;

import ancient.map.WorldMap;
import ancient.players.PlayerManager;
import appStates.AppState;
import appStates.LobbyState;
import appStates.MenuState;
import appStates.PlayState;
import assets.XmlLoader;
import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import controllers.network.NetworkController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import static java.util.logging.Level.*;
import java.util.logging.Logger;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static Main app;

    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;

    private AppState state = null;
    private PlayState playState = null;
    private MenuState menuState = null;
    private LobbyState lobbyState = null;

    private NetworkController netCon;
    private PlayerManager pMgr;

    private final String HUD_XML = "Interface/hud.xml";
    private final String LOBBY_XML = "Interface/lobby.xml";
    private final String MAIN_XML = "Interface/main_menu.xml";

    public static void main(String[] args) {
        app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Logger.getLogger("de.lessvoid").setLevel(WARNING);
        menuState = new MenuState();
        setState(menuState);

        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml(MAIN_XML, "main_menu");
        nifty.addXml(LOBBY_XML);
        nifty.addXml(HUD_XML);
        guiViewPort.addProcessor(niftyDisplay);

        setDisplayStatView(false);
        setDisplayFps(false);
        setPauseOnLostFocus(false);

        assetManager.registerLoader(XmlLoader.class, "xml");

        netCon = new NetworkController();

        pMgr = new PlayerManager();
    }

    @Override
    public void destroy() {
        super.destroy();
        netCon.close();
    }

    /**
     * switches app states and starts playing the game
     */
    public void gotoGame() {
        gotoGame(null);
    }

    public void gotoGame(WorldMap wm) {
        if (playState == null) {
            playState = new PlayState(wm);
        }
        setState(playState);
    }

    public void gotoLobby() {
        if (lobbyState == null) {
            lobbyState = new LobbyState();
        }
        setState(lobbyState);
    }

    public void setState(AppState state) {
        if (this.state != null) {
            stateManager.detach(this.state);
        }
        this.state = state;
        stateManager.attach(state);
    }

    public AppState getState() { return state; }
    public PlayState getPlayState() { return playState; }
    public MenuState getMenuState() { return menuState; }
    public LobbyState getLobbyState() { return lobbyState; }
    public Nifty getNifty() { return nifty; }
    public ScreenController getScreenController() {
        Screen screen = nifty.getCurrentScreen();
        if (screen != null) {
            return screen.getScreenController();
        } else {
            return null;
        }
    }
    public NetworkController getNetworkController() { return netCon; }
    public PlayerManager getPlayerManager() { return pMgr; }
}
