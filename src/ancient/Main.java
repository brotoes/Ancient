package ancient;

import appStates.PlayAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import controllers.ui.GuiController;
import de.lessvoid.nifty.Nifty;
import static java.util.logging.Level.*;
import java.util.logging.Logger;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static Main app;

    public NiftyJmeDisplay niftyDisplay;
    public Nifty nifty;

    private AbstractAppState state = null;
    private PlayAppState playState;
    private GuiController guiController;

    public static void main(String[] args) {
        app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Logger.getLogger("de.lessvoid").setLevel(WARNING);
        playState = new PlayAppState();
        setState(playState);

        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        guiController = new GuiController();
        nifty.fromXml("Interface/gui.xml", "start", guiController);
        guiViewPort.addProcessor(niftyDisplay);
        stateManager.attach(guiController);

        Main.app.setDisplayStatView(false);
        Main.app.setDisplayFps(false);
    }

    public void setState(AbstractAppState state) {
        if (this.state != null) {
            stateManager.detach(this.state);
        }
        this.state = state;
        stateManager.attach(state);
    }

    public AbstractAppState getState() { return state; }
    public PlayAppState getPlayState() { return playState; }
    public GuiController getGuiController() { return guiController; }
}
