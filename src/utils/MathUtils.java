/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.jme3.math.Vector3f;

/**
 *
 * @author brock
 */
public class MathUtils {
    private static float defErr = 0.001f;

    /**
     * tests if, given err, a and b are equal
     * @param a
     * @param b
     * @param err
     * @return
     */
    public static boolean fleq(float a, float b, float err) {
        return Math.abs(a - b) <= err;
    }

    public static boolean fleq(float a, float b) {
        return fleq(a, b, defErr);
    }

    public static boolean veceq(Vector3f a, Vector3f b, float err) {
        return (fleq(a.x, b.x, err) && fleq(a.y, b.y, err) && fleq(a.z, b.z, err));
    }

    public static boolean veceq(Vector3f a, Vector3f b) {
        return veceq(a, b, defErr);
    }

    /**
     * maps x from range a-b to c-d
     *
     * @param x
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    public static float map(float x, float a, float b, float c, float d) {
        return (x - a)/(b - a)*(d - c) + c;
    }

    public static double map(double x, double a, double b, double c, double d) {
        return (x - a)/(b - a)*(d - c) + c;
    }

    public static float map(float x, float c, float d) {
        return map(x, 0.0f, 1.0f, c, d);
    }
}
