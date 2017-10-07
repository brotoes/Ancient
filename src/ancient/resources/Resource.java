/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.resources;

import ancient.Main;
import com.jme3.math.ColorRGBA;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.XMLUtils;

/**
 *
 * @author brock
 */
public class Resource {
    private final static HashMap<String, Resource> resources = new HashMap<>();
    private final static String RESOURCES_XML = "Config/Resources.xml";

    private final String name;
    private int value;
    private ColorRGBA color;

    private Resource(Node node) {
        this.name = node.getNodeName().trim();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i ++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (child.getNodeName()) {
                case "BaseValue":
                    value = Integer.valueOf(child.getTextContent().trim());
                    break;
                case "Color":
                    color = XMLUtils.getColor(child);
                    break;
                default:
                    System.err.println("Warning: Invalid Resource Attribute");
            }
        }
    }

    /**
     * reads XML doc and populates resources list
     */
    public static void parseResources() {
        Document doc = (Document)Main.app.getAssetManager().loadAsset(RESOURCES_XML);

        doc.getDocumentElement().normalize();
        NodeList nodes = doc.getElementsByTagName("Resources");
        nodes = nodes.item(0).getChildNodes();

        /* create resource for each node */
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            resources.put(child.getNodeName().trim(), new Resource(child));
        }
    }

    @Override
    public String toString() {
        return name;
    }

    /* getters and setters */
    public static Resource getResource(String name) { return resources.get(name); }
    public ColorRGBA getColor() { return color; }
}
