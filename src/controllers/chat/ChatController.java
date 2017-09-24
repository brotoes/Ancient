/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.chat;

import ancient.Main;
import controllers.network.messages.ChatMessage;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import network.messages.Message;

/**
 *
 * @author brock
 */
public class ChatController {
    private final Nifty nifty;
    private final Screen screen;
    private final TextField input;
    private final Element output;

    public ChatController(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        input = screen.findNiftyControl("chat_input", TextField.class);
        output = screen.findElementById("chat_output");
    }

    /**
     * sends a chat to a given player
     */
    public void sendChat() {
        String line = input.getRealText();
        Message msg = new ChatMessage(line);
        Main.app.getNetworkController().send(msg);
        input.setText("");
    }

    /**
     * Outputs chat
     * @param line
     */
    public void receiveChat(String line) {
        PanelBuilder pb = new PanelBuilder() {{
            width("100%");
            childLayoutHorizontal();
            text(new TextBuilder() {{
                text(line);
                style("text-style");
            }});
        }};
        pb.build(nifty, screen, output);
    }
}
