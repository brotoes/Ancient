/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.jme3.math.ColorRGBA;
import org.w3c.dom.Node;

/**
 *
 * @author brock
 */
public class XMLUtils {
    /**
     * Takes Node containing three comma delimited integer values between 0-255
     * and returns a ColorRGBA
     * @param node
     */
    public static ColorRGBA getColor(Node node) {
        String[] vals = node.getTextContent().split(",");
        if (vals.length != 3) {
            System.err.println("Warning: Invalid Color String");
            return ColorRGBA.White;
        } else {
            return new ColorRGBA(Float.valueOf(vals[0])/255,
                    Float.valueOf(vals[1])/255,
                    Float.valueOf(vals[2])/255, 1.0f);
        }
    }
}
