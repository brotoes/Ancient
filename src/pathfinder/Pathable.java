/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinder;

import java.util.List;

/**
 *
 * @author brock
 * @param <T> Type of neighbors
 */
public interface Pathable<T> {
    public List<T> getNeighbors();
}
