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
        String address = getElementText("address_field");
        String name = getElementText("name_field");

        if (address != null && name != null) {
            try {
                Main.app.getNetworkController().connect(address, name);
                displayText("Joined Game!");
                Main.app.gotoLobby();
            } catch (JoinException e) {
                displayText(e.getMessage());
            }
        }
    }

    /**
     * Called when "Host" is pressed. Creates game lobby
     */
    public void hostButton() {
        String name = getElementText("name_field");
        try {
            Main.app.getNetworkController().host(name);
            displayText("Hosted Game!");
            Main.app.gotoLobby();
        } catch (JoinException | CreateException e) {
            displayText(e.getMessage());
        }
    }

    /**
     * finds and returns text in element of id 'id'
     * @param id
     * @return
     */
    public String getElementText(String id) {
        Element elem = screen.findElementById(id);
        TextField field;
        String text;

        try {
            field = elem.getNiftyControl(TextField.class);
            text = field.getRealText().trim();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        return text;
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
}