/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import ancient.Main;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import exceptions.CreateException;
import exceptions.JoinException;

/**
 * Controls the Main Menu
 * @author brock
 */
public class MainMenuController implements ScreenController {
    private Nifty nifty;
    private Screen screen;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {}

    @Override
    public void onEndScreen() {}

    /**
     * called when "Join" is pressed. Joins hosted game
     */
    public void joinButton() {
        Element joinElement = screen.findElementById("join_field");
        TextField join;
        String text = null;

        if (joinElement != null) {
            join = joinElement.getNiftyControl(TextField.class);
        } else {
            System.err.println("Error: Element Missing");
            return;
        }

        if (join != null) {
            text = join.getRealText().trim();
        } else {
            System.err.println("Type Error: Element type incorrect");
        }

        if (text != null) {
            try {
                Main.app.getNetworkController().connect(text);
                displayText("Joined Game!");
                gotoLobby();
            } catch (JoinException e) {
                displayText(e.getMessage());
            }
        }
    }

    /**
     * Called when "Host" is pressed. Creates game lobby
     */
    public void hostButton() {
        try {
            Main.app.getNetworkController().host();
            displayText("Hosted Game!");
            gotoLobby();
        } catch (JoinException | CreateException e) {
            displayText(e.getMessage());
        }
    }

    /**
     * displays text in status_text element
     * @param line
     */
    public void displayText(String line) {
        Element statusText = screen.findElementById("status_text");
        TextRenderer status;

        if (statusText != null) {
            status = statusText.getRenderer(TextRenderer.class);
        } else {
            return;
        }

        if (status != null) {
            status.setText(line);
        }
    }

    /**
     * changes the screen to lobby
     */
    private void gotoLobby() {
        Main.app.gotoLobby();
    }
}