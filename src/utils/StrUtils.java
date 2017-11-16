/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jme3.math.ColorRGBA;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import network.messages.EchoMessage;


/**
 *
 * @author brock
 */
public class StrUtils {
    /**
     * Unserialize using the kryo serializer with deep nesting suppoert
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object fromString(String str) throws IOException, ClassNotFoundException {
        Kryo kryo = new Kryo();
        byte[] data = Base64.getDecoder().decode(str);
        Input input = new Input(new ByteArrayInputStream(data));
        return kryo.readClassAndObject(input);
    }

    /**
     * Serialize using the Kryo Serializer with deep nesting support
     * @param obj
     * @return
     * @throws IOException
     */
    public static String toString(Object obj) throws IOException {
        Kryo kryo = new Kryo();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, obj);
        output.flush();

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Unserializes an object using Java serializer
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object fromStringVanilla(String str) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(str);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Serializes an object using Java serializer
     * @param obj
     * @return
     * @throws IOException
     */
    public static String toStringVanilla(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Converts a ColorRGBA to string of format #ffffffff
     * @param color
     * @return
     */
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
