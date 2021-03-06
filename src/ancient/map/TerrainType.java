/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancient.map;

import ancient.Main;
import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.ArrUtils;
import utils.XMLUtils;

/**
 *
 * @author brock
 */
public class TerrainType {
    /* Static Variables */
    private final static String FNAME = "Config/TerrainTypes.xml";
    private final static Map<String, TerrainType> ttypes = new HashMap<>();
    private final static Map<String, TerrainMetric> metricsByName = new HashMap<>();
    private final static Map<String, ArrayList<TerrainMetric>> metricsByCat = new HashMap<>();

    /* Instance Variables */
    private String name;
    private ColorRGBA color;
    private String textureFile;
    private boolean claimable = true;
    private ArrayList<TerrainMetric> metrics = new ArrayList<>();

    /**
     * Traverse XML and create TerrainTypes.
     * Map values to their TerrainTypes
     */
    public static void load() {
        try {
            Document doc = (Document)Main.app.getAssetManager().loadAsset(FNAME);

            doc.getDocumentElement().normalize();

            /* Traverse list of metrics and store */
            NodeList catNodes = doc.getElementsByTagName("Metrics").item(0).getChildNodes();
            for (int i = 0; i < catNodes.getLength(); i ++) {
                Node catNode = catNodes.item(i);
                if (catNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                NodeList metricNodes = catNode.getChildNodes();
                for (int j = 0; j < metricNodes.getLength(); j ++) {
                    Node metricNode = metricNodes.item(j);
                    if (metricNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    TerrainMetric metric = new TerrainMetric(catNode.getNodeName(), metricNode);

                    /* store loaded metric */
                    metricsByName.put(metric.getName(), metric);
                    if (metricsByCat.get(metric.getCategory()) == null) {
                        ArrayList<TerrainMetric> newList = new ArrayList<>();
                        newList.add(metric);
                        metricsByCat.put(metric.getCategory(), newList);
                    } else {
                        metricsByCat.get(metric.getCategory()).add(metric);
                    }
                }
            }

            /* Traverse list of TerrainTypes and store */
            NodeList typeNodes = doc.getElementsByTagName("Types").item(0).getChildNodes();
            for (int i = 0; i < typeNodes.getLength(); i ++) {
                Node typeNode = typeNodes.item(i);
                if (typeNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                TerrainType nextTerrainType = new TerrainType(typeNode);
                ttypes.put(typeNode.getNodeName(), nextTerrainType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * return TerrainType for given values
     *
     * @param elevation
     * @param temperature
     * @return
     */
    public static TerrainType getTerrainType(float elevation, float temperature) {
        ArrayList<TerrainMetric> metrics = new ArrayList<>(2);

        /* find elevation */
        ArrayList<TerrainMetric> elevations = metricsByCat.get("Elevation");
        if (elevations != null) {
            for (int i = 0; i < elevations.size(); i ++) {
                Float min = elevations.get(i).getMin();
                Float max = elevations.get(i).getMax();
                if ((min == null || min <= elevation) &&
                        (max == null || max >= elevation)) {
                    metrics.add(elevations.get(i));
                    break;
                }
            }
        } else {
            System.err.println("Warning: No Elevation Metrics Loaded.");
        }

        /* find temperature */
        ArrayList<TerrainMetric> temps = metricsByCat.get("Temperature");
        if (temps != null) {
            for (int i = 0; i < temps.size(); i ++) {
                Float min = temps.get(i).getMin();
                Float max = temps.get(i).getMax();
                if ((min == null || min <= temperature) &&
                        (max == null || max >= temperature)) {
                    metrics.add(temps.get(i));
                    break;
                }
            }
        } else {
            System.err.println("Warning: No Temperature Metrics Loaded.");
        }

        return getTerrainType(metrics);
    }

    /**
     * return TerrainType corresponding to supplied name
     * @param name
     * @return
     */
    public static TerrainType getTerrainType(String name) {
        return ttypes.get(name);
    }

    /**
     * return TerrainType corresponding to supplied metrics
     * returns null if nothing found
     * @param neededMetrics
     * @return
     */
    public static TerrainType getTerrainType(ArrayList<TerrainMetric> neededMetrics) {
        Iterator i = ttypes.entrySet().iterator();
        while (i.hasNext()) {
            HashMap.Entry<String, TerrainType> pair = (HashMap.Entry)i.next();
            TerrainType curType = pair.getValue();

            if (ArrUtils.isSubset(neededMetrics, curType.metrics)) {
                return curType;
            }
        }
        System.err.println("Warning: No valid metric");
        for (TerrainMetric metric : neededMetrics) {
            System.err.println(metric.getName());
        }
        return null;
    }

    /**
     * Build new TerrainType from XML node
     *
     * @param node
     */
    private TerrainType(Node node) {
        NodeList childNodes = node.getChildNodes();
        name = node.getNodeName().trim();
        for (int i = 0; i < childNodes.getLength(); i ++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (childNode.getNodeName()) {
                case "Color":
                    color = XMLUtils.getColor(childNode);
                    break;
                case "Metrics":
                    processMetrics(childNode);
                    break;
                case "Unclaimable":
                    claimable = false;
                    break;
                case "Texture":
                    textureFile = childNode.getTextContent().trim();
                    break;
            }
        }
    }

    /**
     * no-arg constructor for use by Kryo serializer
     */
    private TerrainType() {}

    /**
     * Takes Node containing valid metrics and adds them to the instance
     */
    private void processMetrics(Node node) {
        NodeList cats = node.getChildNodes();
        for (int i = 0; i < cats.getLength(); i ++) {
            Node cat = cats.item(i);
            if (cat.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String catName = cat.getNodeName();
            String[] metricNames = cat.getTextContent().split(",");
            for (String mName : metricNames) {
                TerrainMetric metric = TerrainType.metricsByName.get(mName);
                if (metric != null) {
                    metrics.add(metric);
                } else {
                    System.err.println("Warning: no such metric");
                }
            }
        }
    }

    public ColorRGBA getColor() { return color; }
    public String getTextureFile() { return textureFile; }
    public String getName() { return name; }
    public boolean isClaimable() { return claimable; }
}
