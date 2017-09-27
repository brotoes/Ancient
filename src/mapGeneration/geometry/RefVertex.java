/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration.geometry;

import com.jme3.math.Vector3f;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pathfinder.Pathable;
import utils.MathUtils;

/**
 * Stores a reference to a vector to allow multiple vertices to
 * point the a linked location
 * @author brock
 */
public class RefVertex implements Pathable, Serializable {
    private static final List<RefVertex> verts = new ArrayList<>();

    private final Vector3f vector;
    /* stores connected RefVertices */
    private final List<RefVertex> neighbors;

    /**
     * creates a vertex from a vector and returns. if equal vertex already
     * exists, it returns the existing one.
     * @param vec
     * @return
     */
    public static RefVertex newVertex(Vector3f vec) {
        for (RefVertex i : verts) {
            if (i.eq(vec)) {
                return i;
            }
        }

        RefVertex vert = new RefVertex(vec);
        verts.add(vert);
        return vert;
    }

    protected RefVertex(Vector3f vec) {
        vector = vec;
        neighbors = new ArrayList<>();
    }


    /**
     * adds a vertex to list of adjacent vertices if not already added
     * @param vert
     */
    public void connect(RefVertex vert) {
        if (!neighbors.contains(vert)) {
            neighbors.add(vert);
            vert.backConnect(this);
        }
    }
    /**
     * creates back connection when connecting vertices
     * @param vert
     */
    private void backConnect(RefVertex vert) {
        if (!neighbors.contains(vert)) {
            neighbors.add(vert);
        }
    }

    public boolean eq(RefVertex other) {
        return MathUtils.veceq(this.getVector(), other.getVector());
    }

    public boolean eq(Vector3f other) {
        return MathUtils.veceq(this.getVector(), other);
    }

    /* getters and setters */
    public static List<RefVertex> getVertices() {
        return Collections.unmodifiableList(verts);
    }

    @Override
    public List<RefVertex> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }

    public Vector3f getVector() { return vector; }
    public float getX() { return vector.getX(); }
    public float getY() { return vector.getY(); }
    public float getZ() { return vector.getZ(); }

    @Override
    public String toString() {
        return vector.toString();
    }
}
