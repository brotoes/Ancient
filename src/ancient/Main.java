package ancient;

import appStates.PlayAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    public static Main app;
    
    private AbstractAppState state;
    private PlayAppState playState;
    
    public static void main(String[] args) {
        app = new Main();
        app.start();
    }

    @Override
    public void initialize() {
        playState = new PlayAppState();
    }
    
    public void setState(AbstractAppState state) {
        
    }
}
