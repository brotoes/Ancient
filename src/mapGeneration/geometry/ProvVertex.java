/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration.geometry;

import ancient.map.Province;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a reference to a vector to allow multiple vertices to
 * point the a linked location
 * @author brock
 */
public class ProvVertex extends RefVertex {
    private static final List<ProvVertex> verts = new ArrayList<>();

    private final List<Province> provs;

    /**
     * creates a vertex from a vector and returns. if equal vertex already
     * exists, it returns the existing one.
     * @param vec
     * @return
     */
    public static ProvVertex newVertex(Vector3f vec) {
        /* find existing vertex or create new */
        ProvVertex vert = null;
        for (ProvVertex i : verts) {
            if (i.eq(vec)) {
                vert = i;
                break;
            }
        }
        if (vert == null) {
            vert = new ProvVertex(vec);
            verts.add(vert);
        }

        return vert;
    }
    public static ProvVertex newVertex(float x, float y, float z) {
        return newVertex(new Vector3f(x, y, z));
    }

    private ProvVertex(Vector3f vec) {
        super(vec);
        provs = new ArrayList<>();
    }

    /**
     * Adds a province to the list of connected provinces.
     * @param prov
     */
    public void addProvince(Province prov) {
        if (!provs.contains(prov)) {
            provs.add(prov);
        }
    }

    /* getters and setters */
    public List<Province> getProvinces() {
        return Collections.unmodifiableList(provs);
    }
    /**
     * Returns true if this vertex lies between provinces of different owners
     * @return
     */
    public boolean isBorder() {
        //TODO: test if on edge of map
        for (int i = 1; i < provs.size(); i ++) {
            if (provs.get(i).getOwner() != provs.get(i - 1).getOwner()) {
                return true;
            }
        }

        return false;
    }
    /**
     * returns a list of provinces shared between this vertex and vert
     * @param vert
     * @return
     */
    public List<Province> getSharedProvinces(ProvVertex vert) {
        List<Province> shared = new ArrayList<>();

        for (Province p : provs) {
            if (vert.getProvinces().contains(p)) {
                shared.add(p);
            }
        }

        return shared;
    }


    /* static */
    /**
     * sorts a list of lists of vertices that are connected sequentially.
     *
     * @param verts
     * @return
     */
    public static List<List<ProvVertex>> sortConnectedVertLoop(List<ProvVertex> verts) {
        List<List<ProvVertex>> result = new ArrayList<>();
        List<ProvVertex> availVerts = new ArrayList(verts);

        /* keep creating new sets of lists until no verts are left */
        while (availVerts.size() > 0) {
            /* intialize next set of verts */
            List<ProvVertex> nextList = new ArrayList<>(availVerts.size());
            result.add(nextList);

            ProvVertex next = availVerts.get(0);
            availVerts.remove(0);
            ProvVertex prev;
            /* keep adding connected verts to this list until this set is done */
            while (next != null) {
                nextList.add(next);
                availVerts.remove(next);

                List<RefVertex> adjs = next.getNeighbors();
                prev = next;
                next = null;
                /* find vertex that is connected by a border edge */
                for (RefVertex i : adjs) {
                    if (i instanceof ProvVertex && !nextList.contains((ProvVertex)i)
                            && availVerts.contains((ProvVertex)i)
                            && !Province.sameOwner(prev.getSharedProvinces((ProvVertex)i))) {
                        next = (ProvVertex)i;
                        break;
                    }
                }
            }
        }

        return result;
    }
}
