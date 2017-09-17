/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appStates;

import ancient.Main;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;

/**
 * Controls game behaviour while in Lobby
 * @author brock
 */
public class LobbyState extends AppState {
    @Override
    public void initialize(AppStateManager sm, Application app) {
        super.initialize(sm, app);

        Main.app.getNifty().gotoScreen("lobby");

        enable();
    }
}
