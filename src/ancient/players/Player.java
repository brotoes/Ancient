/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.players;

import com.jme3.math.ColorRGBA;
import java.io.Serializable;
import ancient.Main;

/**
 *
 * @author brock
 */
public class Player implements Serializable {
    private final int id;
    private String name;
    private int colorInd = 0;
    /* stores if this player is the local player */
    private boolean local;
    private final PlayerManager pm;

    //TODO: Test for these constructors only being called from game host
    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.local = false;
        this.pm = Main.app.getPlayerManager();
    }

    public Player(int id, String name, boolean local) {
        this.id = id;
        this.name = name;
        this.local = local;
        this.pm = Main.app.getPlayerManager();
    }

    public Player(String name) {
        this(0, name, true);
    }

    /**
     * takes data from player
     * @param player
     */
    public void update(Player player) {
        this.name = player.name;
        this.colorInd = player.colorInd;
        Main.app.getPlayerManager().updated(this);
    }

    /* getters and setters */
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        //pm.updated(this);
    }
    public ColorRGBA getColor() {
        return Main.app.getPlayerManager().getColor(colorInd);
    }
    public void setColor(ColorRGBA color) {
        this.colorInd = Main.app.getPlayerManager().getColors().indexOf(color);
       // pm.updated(this);
    }
    public boolean isLocal() { return local; }
    public void setLocal(boolean local) { this.local = local; }

    @Override
    public String toString() {
        return getName();
    }
}
