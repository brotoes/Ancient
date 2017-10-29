/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import ancient.map.WorldMap;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import mapGeneration.geometry.ProvVertex;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DTriangle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.GeometryFilter;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.polygonize.Polygonizer;

/**
 *
 * @author brock
 */
public class GeomUtils {
    /**
     * Takes a list of vertices and converts it into a renderable 3D Mesh
     * @param verts
     * @param width
     * @param map
     * @return
     */
    public static Mesh getBorderMesh(List<ProvVertex> verts, double width, WorldMap map) {
            //FloatBuffer buf = BufferUtils.createFloatBuffer(verts.size()*3);
            List<Coordinate> coords = new ArrayList<>(verts.size()*2);
            verts.stream().forEachOrdered(v ->
                    coords.add(new Coordinate(v.getX(), v.getY())));
            coords.add(new Coordinate(verts.get(0).getX(), verts.get(0).getY()));

            Coordinate[] coordArr = Arrays.copyOf(coords.toArray(), coords.size(), Coordinate[].class);

            /* Compute geometry of border */
            GeometryFactory geomFact = new GeometryFactory();

            Geometry outerGeom = new GeometryFactory().createPolygon(coordArr);
            BufferOp op = new BufferOp(outerGeom);
            Geometry innerGeom = op.getResultGeometry(-width);
            Geometry geom = outerGeom.difference(innerGeom);

            /* compute constraints of triangulation */
            Polygonizer p = new Polygonizer(true);
            p.add(geom);
            Collection<Polygon> polys = p.getPolygons();
            List<LineString> lineStrings = new ArrayList<>();

            polys.stream().forEachOrdered(poly -> {
                lineStrings.add(poly.getExteriorRing());
                lineStrings.add(poly.getInteriorRingN(0));
            });

            LineString[] lsArr = Arrays.copyOf(lineStrings.toArray(), lineStrings.size(), LineString[].class);
            MultiLineString constraints = new MultiLineString(lsArr, geomFact);

            /* Use JDelaunay to Compute polygonized border */
            ConstrainedMesh cmesh = new ConstrainedMesh();
            ArrayList<DEdge> edges = new ArrayList<>();
            constraints.apply(new GeometryFilter() {
                @Override
                public void filter(Geometry g) {
                    if (g instanceof LinearRing) {
                        Coordinate[] c = g.getCoordinates();
                        for (int i = 0; i < c.length - 1; i ++) {
                            Coordinate start = c[i];
                            Coordinate end = c[i + 1];
                            try {
                                edges.add(new DEdge(start.x, start.y, 0.0f, end.x, end.y, 0.0f));
                            } catch (DelaunayError e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                }
            });
            try {
                cmesh.setConstraintEdges(edges);
                cmesh.processDelaunay();
            } catch (DelaunayError e) {
                e.printStackTrace();
                return null;
            }

            List<DTriangle> triangles = cmesh.getTriangleList();
            /*Translate triangles back into JTS geometry */
            List<Geometry> jtsTris = new ArrayList<>();
            triangles.stream().forEach(t -> {
                Coordinate[] shell = new Coordinate[4];
                shell[0] = new Coordinate(t.getPoint(0).getX(), t.getPoint(0).getY());
                shell[1] = new Coordinate(t.getPoint(1).getX(), t.getPoint(1).getY());
                shell[2] = new Coordinate(t.getPoint(2).getX(), t.getPoint(2).getY());
                shell[3] = new Coordinate(t.getPoint(0).getX(), t.getPoint(0).getY());
                jtsTris.add(geomFact.createPolygon(shell));
            });

            /* remove edges outside of boundary */
            List<Float> floats = new ArrayList<>(jtsTris.size()*3);

            jtsTris.stream().filter(g -> geom.covers(g)).forEachOrdered(g -> {
                Coordinate[] result = g.getCoordinates();
                /* Coordinate duplicated at end, don't add it to the buffer */
                /* Correct winding of triangle */
                if (isFacing(result)) {
                    for (int j = 0; j < result.length - 1; j ++) {
                        floats.add((float)result[j].x);
                        floats.add((float)result[j].y);
                        if (map == null) {
                            floats.add(10.0f);
                        } else {
                            floats.add(map.getPointZ((float)result[j].x, (float)result[j].y));
                        }
                    }
                } else {
                    for (int j = result.length - 2; j >= 0; j --) {
                        floats.add((float)result[j].x);
                        floats.add((float)result[j].y);
                        if (map == null) {
                            floats.add(10.0f);
                        } else {
                            floats.add(map.getPointZ((float)result[j].x, (float)result[j].y));
                        }
                    }
                }
            });
            FloatBuffer buf = BufferUtils.createFloatBuffer(floats.size());
            floats.stream().forEachOrdered(f -> buf.put(f));

            Mesh mesh = new Mesh();
            mesh.setMode(Mesh.Mode.Triangles);
            mesh.setBuffer(VertexBuffer.Type.Position, 3, buf);
            mesh.updateBound();

            return mesh;
    }

    /**
     * Determines Triangle Facing
     * @param triangle
     * @return
     */
    public static boolean isFacing(Coordinate[] triangle) {
        Vector3f a = new Vector3f((float)triangle[0].x, (float)triangle[0].y, 0.0f);
        Vector3f b = new Vector3f((float)triangle[1].x, (float)triangle[1].y, 0.0f);
        Vector3f c = new Vector3f((float)triangle[2].x, (float)triangle[2].y, 0.0f);

        Vector3f v = b.subtract(a);
        Vector3f u = c.subtract(a);

        return v.cross(u).getZ() > 0;
    }

    public static void main(String[] args) {
        List<ProvVertex> verts = new ArrayList<>();
        verts.add(ProvVertex.newVertex(0.0f, 0.0f, 0.0f));
        verts.add(ProvVertex.newVertex(10.0f, 0.0f, 0.0f));
        verts.add(ProvVertex.newVertex(10.0f, 10.0f, 0.0f));
        verts.add(ProvVertex.newVertex(0.0f, 10.0f, 0.0f));

        getBorderMesh(verts, 1.0f, null);
    }
}
