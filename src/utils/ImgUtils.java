/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.jme3.texture.Image;
import java.awt.image.BufferedImage;
import jme3tools.converters.ImageToAwt;

/**
 *
 * @author brock
 */
public class ImgUtils {
    /**
     * Converts a JME Image resource into an AWT BufferedImage
     * @param img
     * @return
     */
    public static BufferedImage toBufferedImage(Image img) {
        BufferedImage buf = ImageToAwt.convert(img, false, true, 0);
        /* Flip the buffered image because JME's coordinate system is broken */
        for (int x=0; x < buf.getWidth(); x ++) {
            for (int y = 0; y < buf.getHeight()/2 ; y++) {
                int tmp = buf.getRGB(x, y);
                buf.setRGB(x, y, buf.getRGB(x, buf.getHeight()-y-1));
                buf.setRGB(x, buf.getHeight()-y-1, tmp);
            }
        }
        return buf;
    }
}
