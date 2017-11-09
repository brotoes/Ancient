/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.map;

import ancient.Main;
import ancient.players.Player;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;

/**
 *
 * @author brock
 */
public class Flag {
    private Province province;
    private Player player;

    private static final ColorRGBA POLE_COLOR = ColorRGBA.Brown;

    private transient Mesh flagMesh;
    private transient Box poleMesh;
    private transient Material flagMat;
    private transient Material poleMat;
    private transient Geometry flagGeom;
    private transient Geometry poleGeom;
    private transient Node pivot;
    private transient Node polePivot;
    private transient Node flagPivot;

    public Flag(Province prov, Player player) {
        this.province = prov;
        this.player = player;

        flagMesh = getFlagMesh();
        poleMesh = new Box(0.05f, 0.05f, 2.5f);

        flagMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        poleMat = new Material(Main.app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");

        flagMat.setColor("Color", player.getColor());
        flagMat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

        poleMat.setColor("Diffuse", POLE_COLOR);
        poleMat.setBoolean("UseMaterialColors", true);

        flagGeom = new Geometry("flagGeom", flagMesh);
        poleGeom = new Geometry("poleGeom", poleMesh);

        flagGeom.setMaterial(flagMat);
        poleGeom.setMaterial(poleMat);

        pivot = new Node();
        polePivot = new Node();
        flagPivot = new Node();

        pivot.attachChild(polePivot);
        pivot.attachChild(flagPivot);
        polePivot.attachChild(poleGeom);
        flagPivot.attachChild(flagGeom);
        flagPivot.setLocalTranslation(0.0f, 0.0f, 2.5f);

        updatePosition();
    }

    /**
     * Empty constructor for serializing
     */
    public Flag() {}

    /**
     * updates the position of pivots to account for flags added and removed
     */
    public final void updatePosition() {

    }

    /**
     * Generates the mesh of a flag with the top left of the flag centered at the origin
     * @return
     */
    private Mesh getFlagMesh() {
        Mesh mesh = new Mesh();
        FloatBuffer buf = BufferUtils.createFloatBuffer(9);
        buf.put(new float[] {
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, -0.8f,
            1.0f, 0.0f, -0.4f,
        });

        mesh.setMode(Mesh.Mode.Triangles);
        mesh.setBuffer(VertexBuffer.Type.Position, 3, buf);
        mesh.updateBound();

        return mesh;
    }

    /* getters and setters */
    public Province getProvince() { return province; }
    public Player getPlayer() { return player; }
    public Node getPivot() { return pivot; }
}
