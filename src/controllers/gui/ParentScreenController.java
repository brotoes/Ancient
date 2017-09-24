/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import controllers.chat.ChatController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * Controls the Main Menu
 * @author brock
 */
public class ParentScreenController implements ScreenController {
    protected Nifty nifty;
    protected Screen screen;
    private ChatController chatCon;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        chatCon = new ChatController(nifty, screen);
    }

    @Override
    public void onEndScreen() {}

    /* methods callable by all controllers, for use in all screens */
    public void sendChat() {
        chatCon.sendChat();
    }

    public void receiveChat(String line) {
        chatCon.receiveChat(line);
    }
}
