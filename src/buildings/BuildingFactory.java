/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import map.Province;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Spawns a given building type
 * @author brock
 */
public class BuildingFactory {
    private final static String FNAME = "assets/Config/Buildings.xml";
    private static ArrayList<BuildingFactory> buildingFactories = null;

    private final String name;
    private final List<String> ins = new ArrayList<>();
    private final List<String> outs = new ArrayList<>();
    private String desc = "NO DESCRIPTION";

    /**
     * reads XML document and creates a Factory for each type of building
     */
    private static void parseBuildings() {
        buildingFactories = new ArrayList<>();
        try {
            File xmlFile = new File(FNAME);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();
            NodeList buildingNodes = doc.getElementsByTagName("Buildings");
            buildingNodes = buildingNodes.item(0).getChildNodes();

            /* create factory for each building node */
            for (int i = 0; i < buildingNodes.getLength(); i ++) {
                Node buildingNode = buildingNodes.item(i);
                if (buildingNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                buildingFactories.add(new BuildingFactory(buildingNode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BuildingFactory(Node node) {
        this.name = node.getNodeName();
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i ++) {
            Node child = children.item(i);
            switch (child.getNodeName()) {
                case "Description":
                    desc = child.getTextContent().trim();
                    break;
                case "Input":
                    processResourceList(child, ins);
                    break;
                case "Output":
                    processResourceList(child, outs);
                    break;
                default:
                    System.err.println("Warning: Unrecognized building attribute");
            }
        }
    }

    private void processResourceList(Node node, List<String> list) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i ++) {
            
        }
    }

    public Building getBuilding(Province prov) {
        return new Building(name, desc, prov, ins, outs);
    }

    public String getName() {
        return name;
    }
    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * parses buildingfactories if none have been parsed yet, then returns them.
     * @return list of building factories
     */
    public static ArrayList<BuildingFactory> getBuildingFactories() {
        if (BuildingFactory.buildingFactories == null) {
            BuildingFactory.parseBuildings();
        }
        return buildingFactories;
    }

    /**
     * gets list of buildingFactories and returns ones that can be built
     * by prov
     * @param prov
     * @return
     */
    public static ArrayList<BuildingFactory> getValidBuildingFactories(Province prov) {
        ArrayList<BuildingFactory> allFacs = getBuildingFactories();
        ArrayList<BuildingFactory> validFacs = new ArrayList<>();

        for (int i = 0; i < allFacs.size(); i ++) {
            if (prov.isValid(allFacs.get(i))) {
                validFacs.add(allFacs.get(i));
            }
        }

        return validFacs;
    }
}