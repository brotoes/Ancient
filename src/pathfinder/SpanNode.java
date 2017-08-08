/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinder;

/**
 * Stores a node in a Pathfinder spanning tree.
 * For use by Pathfinder class
 * @author brock
 * @param <T>
 */
public class SpanNode<T extends Pathable> {
    private final T contents;
    private final SpanNode<T> parent;
    
    public SpanNode(T contents, SpanNode<T> parent) {
        this.contents = contents;
        this.parent = parent;
    }
    
    public T getContents() {
        return contents;
    }
    
    public SpanNode<T> getParent() {
        return parent;
    }
}
