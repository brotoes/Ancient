/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buildings;

import java.io.File;
import java.util.ArrayList;
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
    }

    public Building getBuilding() {
        return new Building(name, "Description goes here");
    }

    public String getName() {
        return name;
    }
    public String getDesc() {
        return "Description goes here";
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
