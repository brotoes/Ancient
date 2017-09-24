/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.players;

import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Manages list of players
 * @author brock
 */
public class PlayerManager {
    private final List<Player> players;
    private final List<PlayerChangeListener> listeners;
    private final List<ColorRGBA> colors;

    public PlayerManager() {
        players = new ArrayList<>();
        listeners = new ArrayList<>();
        colors = new ArrayList<>();
        colors.add(ColorRGBA.Red);
        colors.add(ColorRGBA.Blue);
        colors.add(ColorRGBA.Green);
        colors.add(ColorRGBA.Yellow);
        colors.add(ColorRGBA.Orange);
    }

    /**
     * creates and adds player to player list
     * @param id
     * @param name
     * @return
     */
    public Player addPlayer(int id, String name) {
        /* determine if name already exists */
        //TODO
        boolean local = players.isEmpty();
        Player player = new Player(id, name, local);
        player.setColor(nextColor());
        players.add(player);
        listeners.stream().forEach(l -> l.playerAdded(player));

        return player;
    }
    public void addPlayer(Player player) {
        Player existing = getPlayer(player.getId());
        if (existing != null) {
            existing.update(player);
            return;
        }
        player.setLocal(players.isEmpty());
        players.add(player);
        listeners.stream().forEach(l-> l.playerAdded(player));
    }
    /**
     * adds a player and generates an id. Should only be called from host
     * @param name
     * @return
     */
    public Player addPlayer(String name) {
        int id = nextId();
        return addPlayer(id, name);
    }


    /**
     * removes a player from the list of players
     * @param player
     */
    public void removePlayer(Player player) {
        players.remove(player);
        listeners.stream().forEach(l -> l.playerRemoved(player));
    }

    /**
     * informs this object that a player has been updated.
     * Should only be called by Player
     * @param player
     */
    protected void updated(Player player) {
        listeners.stream().forEach(l -> l.playerChanged(player));
    }

    /**
     * adds a listener
     * @param pcl
     */
    public void register(PlayerChangeListener pcl) {
        listeners.add(pcl);
    }

    /**
     * removes a listener
     * @param pcl
     */
    public void unregister(PlayerChangeListener pcl) {
        listeners.remove(pcl);
    }

    /**
     * returns the next unused color
     * @return
     */
    public ColorRGBA nextColor() {
        return colors.stream().filter(c -> !players.stream().anyMatch(p -> p.getColor() == c)).findFirst().get();
    }

    /**
     * cycles player through to the next available color
     * @param player
     * @return
     */
    public ColorRGBA colorCycle(Player player) {
        int index = colors.indexOf(player.getColor());
        ColorRGBA color = null;
        for (int i = (index + 1) % colors.size(); i != index; i = (i + 1) % colors.size()) {
            ColorRGBA c = colors.get(i);
            if (!players.stream().anyMatch(p -> p.getColor().equals(c))) {
                color = c;
                break;
            }
        }
        if (color != null) {
            player.setColor(color);
        }
        return color;
    }
    public ColorRGBA colorCycle(int id) {
        return colorCycle(getPlayer(id));
    }

    /* getters and setters */
    public int nextId() { return players.size(); }
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }
    public Player getPlayer(int id) {
        try {
            return players.stream().filter(p -> p.getId() == id).findAny().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
    public ColorRGBA getColor(int i) { return colors.get(i); }
    public List<ColorRGBA> getColors() { return Collections.unmodifiableList(colors); }
}
