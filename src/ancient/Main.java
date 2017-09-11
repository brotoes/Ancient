package ancient;

import appStates.MenuState;
import appStates.PlayState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
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

    private AbstractAppState state = null;
    private PlayState playState;
    private MenuState menuState;

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

        Main.app.setDisplayStatView(false);
        Main.app.setDisplayFps(false);
    }

    public void initPlayState() {
        playState = new PlayState();
        setState(playState);
    }

    public void setState(AbstractAppState state) {
        if (this.state != null) {
            stateManager.detach(this.state);
        }
        this.state = state;
        stateManager.attach(state);
    }

    public AbstractAppState getState() { return state; }
    public PlayState getPlayState() { return playState; }
    public MenuState getMenuState() { return menuState; }
    public Nifty getNifty() { return nifty; }
    public ScreenController getScreenController() {
        Screen screen = nifty.getCurrentScreen();
        if (screen != null) {
            return screen.getScreenController();
        } else {
            return null;
        }
    }
}
