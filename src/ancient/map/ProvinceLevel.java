/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.map;

import ancient.Main;
import com.jme3.scene.Spatial;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author brock
 */
public class ProvinceLevel {
    private static List<ProvinceLevel> levels = null;
    private final static String FNAME = "assets/Config/ProvinceLevels.xml";

    private final int minBuildings;
    private final String name;
    private final Spatial model;

    public ProvinceLevel(Node node) {
        int min = 0;
        String modelFile = null;
        NodeList childNodes = node.getChildNodes();
        name = node.getNodeName().trim();

        for (int i = 0; i < childNodes.getLength(); i ++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (childNode.getNodeName()) {
                case "MinBuildings":
                    min = Integer.valueOf(childNode.getTextContent().trim());
                    break;
                case "Model":
                    modelFile = childNode.getTextContent().trim();
            }
        }

        if (modelFile == null) {
            model = null;
        } else {
            model = Main.app.getAssetManager().loadModel(modelFile);
            model.rotate(1.57f, 0, 0);
        }

        minBuildings = min;
    }

    /**
     * Loads province levels from xml file
     */
    public static void loadLevels() {
        levels = new ArrayList<>();
        try {
            File xmlFile = new File(FNAME);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList childNodes = doc.getElementsByTagName("ProvinceLevels").item(0).getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i ++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                levels.add(new ProvinceLevel(child));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        levels.sort((ProvinceLevel a, ProvinceLevel b) ->
                b.getMinBuildings() - a.getMinBuildings());
    }

    /* getters and setters */
    public int getMinBuildings() { return minBuildings; }
    public String getName() { return name; }
    public Spatial getModel() {
        if (model != null) {
            return model.clone();
        } else {
            return null;
        }
    }

    /**
     * returns largest level for which there are enough buildings
     * @param buildingCount
     * @return
     */
    public static ProvinceLevel getLevel(int buildingCount) {
        if (levels == null) {
            loadLevels();
        }
        for (ProvinceLevel level : levels) {
            if (buildingCount >= level.getMinBuildings()) {
                return level;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
