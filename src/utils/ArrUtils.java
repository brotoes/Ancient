/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;

/**
 *
 * @author brock
 */
public class ArrUtils {
    /**
     * determines if a is a subset of b
     * returns true if all elements of a are contained in b
     * @param <T>
     * @param a subset
     * @param b superset
     * @return 
     */
    public static <T> boolean isSubset(T[] a, T[] b) {
        if (a.length > b.length) {
            return false;
        }
        
        for (int i = 0; i < a.length; i ++) {
            boolean found = false;
            for (int j = 0; j < b.length; j ++) {
                if (a[i].equals(b[j])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
    
    public static <T> boolean isSubset(ArrayList<T> a, ArrayList<T> b) {
        if (a.size() > b.size()) {
            return false;
        }
        
        for (int i = 0; i < a.size(); i ++) {
            boolean found = false;
            for (int j = 0; j < b.size(); j ++) {
                if (a.get(i).equals(b.get(j))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
    
    public static float[] castToFloat(double[] arr) {
        float[] newArr = new float[arr.length];
        for (int i = 0; i < arr.length; i ++) {
            newArr[i] = (float)arr[i];
        }
        
        return newArr;
    }
}
