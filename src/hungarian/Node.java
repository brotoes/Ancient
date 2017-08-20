/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hungarian;

import java.util.ArrayList;
import java.util.List;

/**
 * Node in a graph representing object T
 * @author brock
 * @param <T>
 */
public class Node <T extends Matchable>{
    private final T obj;
    private final ArrayList<Edge> edges;
    /* stores if node is on the isLeft or right */
    private final boolean isLeft;
    private Node match = null;

    private boolean marked = false;

    public Node(T obj, boolean isLeft) {
        this.obj = obj;
        edges = new ArrayList<>();
        this.isLeft = isLeft;
    }

    /* Methods for marking node */
    public void mark() {
        marked = true;
    }
    public void unmark() {
        marked = false;
    }
    public boolean isMarked() {
        return marked;
    }

    /* Methods for manipulating edges */
    /**
     * Adds a new edge to Node. checks that edge does not exist
     * @param node
     */
    public void addEdge(Node node) {
        if (this == node) {
            return;
        }
        for (Edge e : edges) {
            if (e.contains(node)) {
                return;
            }
        }
        int weight = obj.getCost(node.getObj());
        Edge edge = new Edge(this, node, weight);
        addEdge(edge);
        node.addEdge(edge);
    }

    public void addEdge(Edge edge) {
        if (!edges.contains(edge)) {
            edges.add(edge);
        }
    }

    /**
     * reduces weight of all edges by passed weight
     * @param weight
     */
    public void reduceEdges(int weight) {
        for (Edge i : edges) {
            i.subtractWeight(weight);
        }
    }

    /**
     * returns the edge with the least weight
     * @return
     */
    public Edge getLeastEdge() {
        Edge curEdge = null;
        for (Edge e : edges) {
            if (curEdge == null || e.getWeight() < curEdge.getWeight()) {
                curEdge = e;
            }
        }
        return curEdge;
    }

    /**
     * returns the number of edges with zero weights that are unmarked
     * @return
     */
    public int getNumZeros() {
        int count = 0;
        for (Edge e : getEdges()) {
            if (e.getWeight() == 0 && e.getMarks() == 0) {
                count ++;
            }
        }
        return count;
    }

    /**
     * returns list of connected Nodes
     * @return
     */
    public List<Node> getConnected() {
        List<Node> connected = new ArrayList<>();
        for (Edge e : edges) {
            if (isLeft) {
                connected.add(e.getRight());
            } else {
                connected.add(e.getLeft());
            }
        }
        return connected;
    }

    /**
     * returns list of Nodes connected by edge of weight zero
     * @return
     */
    public List<Node> getZeroConnected() {
        List<Node> connected = new ArrayList<>();
        for (Edge e : edges) {
            if (e.getWeight() == 0) {
                if (isLeft) {
                    connected.add(e.getRight());
                } else {
                    connected.add(e.getLeft());
                }
            }
        }
        return connected;
    }

    /**
     * return slist of Nodes connected by edge of weight zero and unmarked
     */
    public void findMatch() {
        List<Node> connected = new ArrayList<>();
        for (Edge e : edges) {
            if (e.getWeight() == 0 && e.getMarks() == 0) {
                if (isLeft) {
                    connected.add(e.getRight());
                } else {
                    connected.add(e.getLeft());
                }
            }
        }

        if (connected.size() > 0) {
            setMatch(connected.get(0));
            match.setMatch(this);
            mark();
            match.mark();
        }
    }

    /* getters and setters */
    public T getObj() { return obj; }
    public List<Edge> getEdges() { return edges; }
    public void setMatch(Node match) {
        if (this.match == null) {
            this.match = match;
            obj.setMatch(match.getObj());
        } else {
            System.err.println("Warning: Double Match");
        }
    }
    public Node getMatch() { return match; }

    @Override
    public String toString() {
        return obj.toString();
    }
}
