/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hungarian;

/**
 *
 * @author brock
 */
public interface Matchable {
    public int getCost(Matchable obj);
    public void setMatch(Matchable match);
}
