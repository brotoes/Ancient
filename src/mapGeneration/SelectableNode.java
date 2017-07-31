/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration;

import com.jme3.scene.Node;

/**
 *
 * @author brock
 */
public class SelectableNode extends Node {
    public final Selectable selectable;
    
    public SelectableNode(String name, Selectable selectable) {
        super(name);

        this.selectable = selectable;
    }
}
