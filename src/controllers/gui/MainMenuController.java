/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import network.Client;
import network.Server;

/**
 * Controls the Main Menu
 * @author brock
 */
public class MainMenuController implements ScreenController {
    private Nifty nifty;
    private Screen screen;

    private Client client;

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
        //Main.app.initPlayState();
        Element joinElement = screen.findElementById("join_field");
        Element statusText = screen.findElementById("join_status_text");
        TextField join;
        TextRenderer status;
        String text = "";

        if (joinElement != null && statusText != null) {
            join = joinElement.getNiftyControl(TextField.class);
            status = statusText.getRenderer(TextRenderer.class);
        } else {
            System.err.println("Error: Element Missing");
            return;
        }

        if (status != null && join != null) {
            text = join.getRealText();
            status.setText(text);
        } else {
            System.err.println("Type Error: Element type incorrect");
        }

        //client = new Client(text);
        //client.start();
    }

    /**
     * Called when "Host" is pressed. Creates game lobby
     */
    public void hostButton() {
        //Server srv = new Server();
        //srv.listen();
    }

    public void sendButton() {
        //client.send("Message");
    }
}
