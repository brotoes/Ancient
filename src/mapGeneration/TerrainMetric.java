/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapGeneration;

import org.w3c.dom.Node;

/**
 *
 * @author brock
 */
public class TerrainMetric {
    private String category;
    private String name;
    private Float min;
    private Float max;
    
    /**
     * Takes category and XML node of format <Name>min,max<Name>
     * if min or max is empty, will set to null and assume infinite
     * if there is no comma, assume min is being supplied
     * 
     * @param category: Name of category
     * @param node: XML node constructed from
     */
    public TerrainMetric(String category, Node node) {
        name = node.getNodeName();
        this.category = category;
        String content = node.getTextContent().trim();
        String[] vals = content.split(",");
        
        if (vals.length == 2) {
            if (vals[0].length() == 0) {
                min = null;
            } else {
                min = Float.valueOf(vals[0]);
            }
            if (vals[1].length() == 0) {
                max = null;
            } else {
                max = Float.valueOf(vals[1]);
            }
        } else {
            min = Float.valueOf(vals[0]);
            max = null;
        }
    }
    
    public String getCategory() { return category; }
    public String getName() { return name; }
    public Float getMin() { return min; }
    public Float getMax() { return max; }
}
