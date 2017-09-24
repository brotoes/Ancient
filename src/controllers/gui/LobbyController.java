/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import ancient.Main;
import ancient.players.Player;
import ancient.players.PlayerChangeListener;
import controllers.network.messages.PlayerUpdateMessage;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.tools.Color;
import java.util.List;
import network.messages.Message;
import utils.StrUtils;

/**
 * Controls the Main Menu
 * @author brock
 */
public class LobbyController extends ParentScreenController implements PlayerChangeListener {
    @Override
    public void bind(Nifty nifty, Screen screen) {
        super.bind(nifty, screen);
        Main.app.getPlayerManager().register(this);
    }

    @Override
    public void onStartScreen() {
        super.onStartScreen();
        updatePlayers();
    }

    @Override
    public void playerAdded(Player player) {
        System.out.println(player.getName());
        Element elem = screen.findElementById("player_list");
        addPlayerPanel(player, elem);
    }

    @Override
    public void playerRemoved(Player player) {
        updatePlayers();
    }

    @Override
    public void playerChanged(Player player) {
        updatePlayers();
    }

    /**
     * clears and repopulates the list of players
     */
    private void updatePlayers() {
        List<Player> players = Main.app.getPlayerManager().getPlayers();
        try {
            Element elem = screen.findElementById("player_list");
            elem.getChildren().stream().forEach(e -> e.markForRemoval());
            players.stream().forEach(p -> addPlayerPanel(p, elem));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void addPlayerPanel(Player player, Element elem) {
        /* build interface element */
        String idStart = "playerpanel_";
        String id = idStart + player.getId();
        PanelBuilder pb = new PanelBuilder(id) {{
            height("46px");
            width("100%");
            childLayoutHorizontal();
            style("panel-dark");
            valignCenter();
            text(new TextBuilder() {{
                text(player.getName());
                style("text-style");
            }});
            panel(new PanelBuilder() {{ width("30px"); }});
            panel(getColorPicker(player));
            panel(new PanelBuilder() {{
                width("*");
            }});
            image(new ImageBuilder() {{
                filename("Interface/Images/not-ready.png");
            }});
        }};
        /* find index to insert the new player */
        int i;
        for (i = 0; i <= elem.getChildrenCount(); i ++) {
            if (i == elem.getChildrenCount()) {
                break;
            }
            //get numerical value of id string
            int idInt = Integer.valueOf(elem.getChildren().get(i).getId().substring(idStart.length()));
            if (player.getId() < idInt) {
                break;
            }
        }
        /* insert */
        Element panel = pb.build(nifty, screen, elem, i);
    }

    private PanelBuilder getColorPicker(Player player) {
        String id = getColorPanelId(player);
        if (player.isLocal()) {
            /* if local player, return picker */
            return new PanelBuilder(id) {{
                height("100%");
                width("60px");
                backgroundColor(StrUtils.hexString(player.getColor()));
                interactOnClick("colorCycle(" + player.getId() + ")");
            }};
        } else {
            /* if other player, return indicator block */
            return new PanelBuilder(id) {{
                height("100%");
                width("60px");
                backgroundColor(StrUtils.hexString(player.getColor()));
            }};
        }
    }

    /**
     * Changes color of player id
     * @param id
     */
    public void colorCycle(String id) {
        /* change player color and panel color to correspond */
        Player player = Main.app.getPlayerManager().getPlayer(Integer.valueOf(id));
        Main.app.getPlayerManager().colorCycle(player);
        //TODO: this shouldn't be here. the code to handle player updates already
        //exists in playerManager.updated
        setPanelColor(player);

        /* update everyone on the new color */
        Message msg = new PlayerUpdateMessage(player);
        Main.app.getNetworkController().send(msg);
    }

    public void setPanelColor(Player player) {
        Element panel = screen.findElementById(getColorPanelId(player));
        Color color = new Color(StrUtils.hexString(player.getColor()));
        panel.getRenderer(PanelRenderer.class).setBackgroundColor(color);
    }

    public String getColorPanelId(Player player) {
        return "colorpicker_" + player.getId();
    }

    public void startGame() {
        if (Main.app.getNetworkController().isHost()) {
            Main.app.gotoGame();
        }
    }
}
