/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Finds paths to root node.
 * @author brock
 * @param <T>
 */
public class Pathfinder<T extends Pathable> {
    private final HashMap<T, SpanNode<T>> nodes = new HashMap<>();

    /**
     * Instantiates Pathfinder object with spanning tree from root
     *
     * @param root
     */
    public Pathfinder(T root) {
        getSpanningTree(root);
    }

    /**
     * Generates a spanning tree and stores nodes in nodes
     * @param elem
     */
    private void getSpanningTree(T elem) {
        Queue<SpanNode<T>> queue = new LinkedList<>();

        queue.add(new SpanNode<>(elem, null));

        while (queue.size() > 0) {
            SpanNode<T> next = queue.remove();
            ArrayList<T> adjs = next.getContents().getNeighbors();
            for (T i : adjs) {
                if (!nodes.containsKey(i)) {
                    SpanNode<T> node = new SpanNode<>(i, next);
                    queue.add(node);
                    nodes.put(i, node);
                }
            }
        }
    }

    /**
     * returns path from node to root
     * @param start
     * @return
     */
    public ArrayList<T> getPath(T start) {
        ArrayList<T> path = new ArrayList<>();
        SpanNode<T> nextNode = nodes.get(start);

        while(nextNode != null) {
            path.add(nextNode.getContents());
            nextNode = nextNode.getParent();
        }

        return path;
    }
}
