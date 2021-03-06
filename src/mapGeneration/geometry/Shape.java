/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration.geometry;

import ancient.map.Province;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import mapGeneration.Voronoi;
import utils.MathUtils;

/**
 *
 * @author brock
 */
public class Shape {
    private ProvVertex center;
    private List<ProvVertex> verts;
    private Province prov;
    private List<Shape> neighbors;

    public Shape(Voronoi vor, int ind, float[] zPoints, float centerZ) {
        /* get data from voronoi */
        PolygonSimple polygon = vor.getPolygon(ind);
        verts = new ArrayList<>(polygon.getNumPoints());

        center = ProvVertex.newVertex(
                (float)polygon.getCentroid().getX(),
                (float)polygon.getCentroid().getY(),
                centerZ);

        double[] xPoints = polygon.getXPoints();
        double[] yPoints = polygon.getYPoints();

        /* add vertices */
        for (int i = 0; i < polygon.getNumPoints(); i ++) {
            verts.add(ProvVertex.newVertex(
                    (float)xPoints[i],
                    (float)yPoints[i],
                    zPoints[i]
            ));
        }

        /* connect vertices */
        for (int i = 1; i < verts.size(); i ++) {
            verts.get(i - 1).connect(verts.get(i));
            verts.get(i).connect(center);
        }
        verts.get(0).connect(verts.get(verts.size() - 1));
        verts.get(0).connect(center);
    }

    /**
     * no-arg constructor for use by Kryo Serializer
     */
    public Shape() {}

    /**
     * after all shapes have been initialized, populate neighbors
     * @param vor
     * @param ind
     */
    public void initNeighbors(Voronoi vor, int ind) {
        neighbors = vor.getNeighbors(ind);
    }

    /* getters and setters */
    public FloatBuffer getVertexBuffer() {
        FloatBuffer buf = BufferUtils.createFloatBuffer((verts.size() + 2)*3);

        buf.put(center.getX());
        buf.put(center.getY());
        buf.put(center.getZ());

        for (int i = 0; i < verts.size(); i ++) {
            buf.put(verts.get(i).getX());
            buf.put(verts.get(i).getY());
            buf.put(verts.get(i).getZ());
        }

        buf.put(verts.get(0).getX());
        buf.put(verts.get(0).getY());
        buf.put(verts.get(0).getZ());

        return buf;
    }

    public FloatBuffer getOutlineBuffer() {
        FloatBuffer buf = BufferUtils.createFloatBuffer(verts.size()*3);

        for (int i = 0; i < verts.size(); i ++) {
            buf.put(verts.get(i).getX());
            buf.put(verts.get(i).getY());
            buf.put(verts.get(i).getZ());
        }

        return buf;
    }

    public FloatBuffer getNormalBuffer() {
        FloatBuffer buf = BufferUtils.createFloatBuffer((verts.size() + 2)*3);

        buf.put(0.0f);
        buf.put(0.0f);
        buf.put(0.0f);

        Vector3f avgVec = new Vector3f(0, 0, 0);
        Vector3f firstVec = new Vector3f(0, 0, 0);

        for (int i = 0; i < verts.size(); i ++) {
            int n = (i + 1) % verts.size();
            Vector3f vec = new Vector3f(verts.get(i).getVector().subtract(center.getVector()));
            Vector3f nVec = new Vector3f(verts.get(n).getVector().subtract(center.getVector()));
            Vector3f normal = vec.cross(nVec).normalize();
            if (i == 0) {
                firstVec = normal;
            }
            avgVec.addLocal(normal);

            buf.put(normal.getX());
            buf.put(normal.getY());
            buf.put(normal.getZ());
        }
        avgVec.normalize();

        buf.put(firstVec.getX());
        buf.put(firstVec.getY());
        buf.put(firstVec.getZ());

        buf.put(0, avgVec.x);
        buf.put(1, avgVec.y);
        buf.put(2, avgVec.z);

        return buf;
    }

    public FloatBuffer getTextureBuffer(int w, int h) {
        FloatBuffer buf = BufferUtils.createFloatBuffer((verts.size() + 2)*2);

        buf.put(center.getX()/w);
        buf.put(center.getY()/h);

        for (int i = 0; i < verts.size(); i ++) {
            buf.put(verts.get(i).getX()/w);
            buf.put(verts.get(i).getY()/h);
        }

        buf.put(verts.get(0).getX()/w);
        buf.put(verts.get(0).getY()/h);

        return buf;
    }

    public IntBuffer getIndexBuffer() {
        IntBuffer buf = BufferUtils.createIntBuffer((verts.size() + 2));

        for (int i = 0; i < buf.limit(); i ++) {
            buf.put(i);
        }

        return buf;
    }

    /**
     * returns a mesh corresponding to the shape's vertices
     * @param w
     * @param h
     * @return
     */
    public Mesh getFaceMesh(int w, int h) {
        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.TriangleFan);
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, getTextureBuffer(w, h));
        mesh.setBuffer(VertexBuffer.Type.Position, 3, getVertexBuffer());
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, getNormalBuffer());
        //mesh.setBuffer(VertexBuffer.Type.Index, 1, getIndexBuffer());
        mesh.updateBound();



        return mesh;
    }
    public Mesh getOutlineMesh() {
        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.LineLoop);
        mesh.setBuffer(VertexBuffer.Type.Position, 3, getOutlineBuffer());
        mesh.updateBound();

        return mesh;
    }

    public List<ProvVertex> getVertices() {
        return Collections.unmodifiableList(verts);
    }

    public List<Shape> getAdjShapes() {
        return Collections.unmodifiableList(neighbors);
    }

    public Province getProvince() { return prov; }
    public void setProvince(Province prov) { this.prov = prov; }
    public ProvVertex getCenter() { return center; }

    /**
     * returns a list of vertices that border a group of shapes
     * @param shapes
     * @return
     */
    public static List<ProvVertex> getBorderVerts(List<Shape> shapes) {
        List<ProvVertex> verts = new ArrayList<>(shapes.size()*10);

        /* assemble all border vertices in list */
        for (Shape shape : shapes) {
            for (ProvVertex vert : shape.getVertices()) {
                if (!verts.contains(vert) && vert.isBorder()) {
                    verts.add(vert);
                }
            }
        }

        return verts;
    }
}
