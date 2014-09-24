package com.cab404.moonlight.util;

/**
 * U is for the Utils
 * Куча статичных методов и переменных (по идее).
 *
 * @author cab404
 */
public class U {
    /**
     * Finds random entry in array. Yup, that's all. :D
     */
    public static <T> T getRandomEntry(T[] values) {
        return values[(int) Math.floor(Math.random() * values.length)];
    }

    /**
     * If you are parsing numbers like +23 on Android, then it's for you.
     */
    public static int parseInt(String in) {
        return Integer.parseInt(in.replace("+", ""));
    }

    /**
     * If you are parsing numbers like +23.468 on Android, then it's for you.
     */
    public static float parseFloat(String in) {
        return Float.parseFloat(in.replace("+", ""));
    }

}
