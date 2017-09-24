/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import ancient.Main;
import ancient.players.Player;
import ancient.players.PlayerChangeListener;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.tools.Color;
import java.util.List;
import utils.StrUtils;

/**
 * Controls the Main Menu
 * @author brock
 */
public class LobbyController implements ScreenController, PlayerChangeListener {
    private Nifty nifty;
    private Screen screen;

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        Main.app.getPlayerManager().register(this);
    }

    @Override
    public void onStartScreen() {
        updatePlayers();
    }

    @Override
    public void onEndScreen() {}

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
        String id = "colorpicker_" + player.getId();
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
        ColorRGBA color = Main.app.getPlayerManager().colorCycle(Integer.valueOf(id));
        String panelId = "colorpicker_" + id;
        Element panel = screen.findElementById(panelId);
        Color col = new Color(StrUtils.hexString(color));
        panel.getRenderer(PanelRenderer.class).setBackgroundColor(col);
    }
}
