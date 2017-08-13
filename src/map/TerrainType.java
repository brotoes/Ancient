/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import com.jme3.math.ColorRGBA;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.ArrUtils;

/**
 *
 * @author brock
 */
public class TerrainType {
    /* Static Variables */
    private final static String FNAME = "assets/Config/TerrainTypes.xml";
    private final static HashMap<String, TerrainType> TYPES = new HashMap<>();
    private final static HashMap<String, TerrainMetric> METRICS_NAME = new HashMap<>();
    private final static HashMap<String, ArrayList<TerrainMetric>> METRICS_CAT = new HashMap<>();
    
    /* Instance Variables */
    private String name;
    private ColorRGBA color;
    private ArrayList<TerrainMetric> metrics = new ArrayList<>();
    
    /**
     * Traverse XML and create TerrainTypes. 
     * Map values to their TerrainTypes
     */
    public static void load() {
        try {
            /* open doc */
            File xmlFile = new File(FNAME);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            
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
                    METRICS_NAME.put(metric.getName(), metric);
                    
                    if (METRICS_CAT.get(metric.getCategory()) == null) {
                        ArrayList<TerrainMetric> newList = new ArrayList<>();
                        newList.add(metric);
                        METRICS_CAT.put(metric.getCategory(), newList);
                    } else {
                        METRICS_CAT.get(metric.getCategory()).add(metric);
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
                TYPES.put(typeNode.getNodeName(), nextTerrainType);
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
        ArrayList<TerrainMetric> elevations = METRICS_CAT.get("Elevation");
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
        ArrayList<TerrainMetric> temps = METRICS_CAT.get("Temperature");
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
            System.err.println("Warning No Temperature Metrics Loaded.");
        }
        
        return getTerrainType(metrics);
    }
    
    /**
     * return TerrainType corresponding to supplied name
     * @param name
     * @return 
     */
    public static TerrainType getTerrainType(String name) {
        return TYPES.get(name);
    }
    
    /**
     * return TerrainType corresponding to supplied metrics
     * returns null if nothing found
     * @param neededMetrics
     * @return 
     */
    public static TerrainType getTerrainType(ArrayList<TerrainMetric> neededMetrics) {
        Iterator i = TYPES.entrySet().iterator();
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
        name = node.getNodeName();
        for (int i = 0; i < childNodes.getLength(); i ++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (childNode.getNodeName()) {
                case "Color":
                    processColor(childNode);
                    break;
                case "Metrics":
                    processMetrics(childNode);
                    break;
            }
        }
    }

    /* Functions to process node types */
    
    /**
     * Takes Color node and assigns color
     */
    private void processColor(Node node) {
        String[] vals = node.getTextContent().split(",");
        if (vals.length != 3) {
            System.err.println("Warning: Invalid Color String");
            color = ColorRGBA.White;
        } else {
            color = new ColorRGBA(Float.valueOf(vals[0])/255,
                    Float.valueOf(vals[1])/255,
                    Float.valueOf(vals[2])/255, 1.0f);
        }
    }
    
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
            for (int j = 0; j < metricNames.length; j ++) {
                TerrainMetric metric = METRICS_NAME.get(metricNames[j]);
                if (metric != null) {
                    metrics.add(metric);
                } else {
                    System.err.println("Warning: no such metric");
                }
            }
        }
    }
    
    public ColorRGBA getColor() { return color; }
    public String getName() { return name; }
}
