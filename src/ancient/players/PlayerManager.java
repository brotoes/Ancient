/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages list of players
 * @author brock
 */
public class PlayerManager {
    private final List<Player> players;
    private final List<PlayerChangeListener> listeners;

    public PlayerManager() {
        players = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    /**
     * adds a player to the list of players
     * @param player
     */
    public void addPlayer(Player player) {
        players.add(player);
        listeners.stream().forEach(l -> l.playerAdded(player));
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

    /* getters and setters */
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }
}
