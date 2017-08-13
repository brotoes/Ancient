/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildings;

/**
 *
 * @author brock
 */
public class Building {
    private final String name;
    private final String desc;

    protected Building(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String toString() { return getName(); }

    /* getters and setters */
    public String getName() { return name; }
    public String getDesc() {
        return desc;
    }
}
