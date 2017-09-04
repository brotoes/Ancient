/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

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

    /**
     * filters a stream to not have duplicates as defined by keyExtractor
     *
     * @param <T>
     * @param keyExtractor
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * removes duplicates from a list, where duplicates are defined by comp
     * @param <T>
     * @param list
     * @param comp
     */
    public static <T> void removeDuplicates(List<T> list,
            BiPredicate<T, T> comp) {
        List<Integer> forRemoval = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i ++) {
            for (int j = i + 1; j < list.size(); j ++) {
                if (comp.test(list.get(i), list.get(j))) {
                    forRemoval.add(j);
                }
            }
        }

        Integer[] rem = (Integer[])forRemoval.stream().distinct().toArray();
        for (int i = rem.length - 1; i >= 0; i --) {
            list.remove(rem[i].intValue());
        }
    }
}
