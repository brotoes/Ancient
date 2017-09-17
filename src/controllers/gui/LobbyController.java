/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.gui;

import ancient.Main;
import ancient.players.Player;
import ancient.players.PlayerChangeListener;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.elements.Element;
import java.util.List;

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
    public void onStartScreen() {}

    @Override
    public void onEndScreen() {}

    @Override
    public void playerAdded(Player player) {
        updatePlayers();
    }

    @Override
    public void playerRemoved(Player player) {
        updatePlayers();
    }

    @Override
    public void playerChanged(Player player) {
        updatePlayers();
    }

    private void updatePlayers() {
        List<Player> players = Main.app.getPlayerManager().getPlayers();
        try {
            Element elem = screen.findElementById("player_list");
            elem.getChildren().stream().forEach(e -> e.markForRemoval());
            players.stream().forEach(p -> addPlayerPanel(p, elem));
            System.out.println(players.size());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void addPlayerPanel(Player player, Element elem) {
        PanelBuilder pb = new PanelBuilder() {{
            height("25px");
            width("*");
            backgroundColor("#777f");
            childLayoutHorizontal();
            text(new TextBuilder() {{
                text(player.getName());
                style("text-style");
            }});
        }};

        pb.build(nifty, screen, elem);
    }
}
