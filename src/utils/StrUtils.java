/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.jme3.math.ColorRGBA;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 *
 * @author brock
 */
public class StrUtils {
    public static Object fromString(String str) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(str);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    public static String toString(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static String hexString(ColorRGBA color) {
        String result = "#";

        for (int i = 0; i < color.getColorArray().length; i ++) {
            float floatVal = color.getColorArray()[i];
            int intVal = (int)(floatVal*255f);
            String strVal = Integer.toHexString(intVal);
            if (strVal.length() < 2) {
                strVal = "0" + strVal;
            } else if (strVal.length() > 2) {
                strVal = "ff";
            }

            result += strVal;
        }

        return result;
    }
}
