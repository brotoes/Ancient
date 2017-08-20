/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Comparator;
import java.util.List;

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

    public static <T> boolean isSubset(List<T> a, List<T> b) {
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

    /**
     * inserts an element while maintaing the list's sorted status.
     * assumes list is already sorted.
     * @param <T>
     * @param list
     * @param item
     * @param comp
     */
    public static <T> void insertSorted(List<T> list, T item, Comparator<T> comp) {
        for (int i = 0; i < list.size(); i ++) {
            if (comp.compare(list.get(i), item) > 0) {
                list.add(i, item);
                return;
            }
        }
        list.add(item);
    }
}
