/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.players;

import com.jme3.math.ColorRGBA;
import ancient.Main;

/**
 *
 * @author brock
 */
public class Player {
    private int id;
    private String name;
    private int colorInd = 0;
    /* stores if this player is the local player */
    private boolean local;
    private boolean ready = false;

    /* convenience var */
    private transient PlayerManager pm;


    //TODO: Test for these constructors only being called from game host
    public Player(int id, String name) {
        this(id, name, false);
    }

    public Player(int id, String name, boolean local) {
        this.id = id;
        this.name = name;
        this.pm = Main.app.getPlayerManager();

        this.local = local;
    }

    public Player(String name) {
        this(0, name, true);
    }

    /**
     * zero-arg constructor for use by Kryo serializer
     */
    public Player() {}

    /**
     * takes data from player
     * @param player
     */
    public void update(Player player) {
        this.name = player.name;
        this.colorInd = player.colorInd;
        this.ready = player.ready;
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
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
    public boolean toggleReady() {
        ready = !ready;
        return ready;
    }

    @Override
    public String toString() {
        return getName();
    }
}
