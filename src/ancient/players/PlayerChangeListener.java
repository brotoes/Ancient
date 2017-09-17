/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.players;

/**
 * Listens for players being added, removed, or changed
 * @author brock
 */
public interface PlayerChangeListener {
    public void playerAdded(Player player);
    public void playerRemoved(Player player);
    public void playerChanged(Player player);
}
