/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.players;

import com.jme3.math.ColorRGBA;
import java.io.Serializable;

/**
 *
 * @author brock
 */
public class Player implements Serializable {
    private String name;
    private ColorRGBA color;

    public Player(String name) {
        this.name = name;
        this.color = ColorRGBA.Red;
    }

    /* getters and setters */
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ColorRGBA getColor() { return color; }
    public void setColor(ColorRGBA color) { this.color = color; }
}
