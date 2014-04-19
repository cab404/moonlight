package com.cab404.moonlight.tests;

import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.SU;
import com.cab404.moonlight.util.U;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Простой тест запросов.
 *
 * @author cab404
 */
public abstract class Test {

    public Test() {}

    static String[] words = {
            "ponym", "lipsum", "celestium", "celenum", "melatio",
            "dissum", "cellum", "ocavium", "orhi", "pegasy", "nio",
            "somnium", "alloc", "catesis", "vecta", "nec", "gracio",
            "detrium", "pacle", "lattesu", "dastio", "reneo", "cyclo",
            "licsyus", "necato", "cellimben", "memleac", "<strong>ERR</strong>",
            "#42", "#53", "#64", "#75", "#86", "86+11", "22+[n-1]", "&larr;&rarr;"
    };

    protected static String requestPassword(String request) {
        Console console = System.console();
        if (console == null)
            return requestString(request);
        else {
            System.out.print(request + "> ");
            return String.valueOf(console.readPassword());
        }
    }

    protected static String requestString(String request) {
        System.out.print(request + "> ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    protected static int requestInt(String request) {return Integer.parseInt(requestString(request));}
    protected static float requestFloat(String request) {return Float.parseFloat(requestString(request));}

    protected static String generateText(int words_num) {
        List<String> text = new ArrayList<>();
        StringBuilder built = new StringBuilder();

        for (int i = 0; i < words_num; i++) {
            String random;
            while (
                    (i != 0 || (random = U.getRandomEntry(words)) == null)
                            &&
                            text.get(i - 1).equals(random = U.getRandomEntry(words))
                    )
                ;
            text.add(random);
        }

        for (int i = 0; i < words_num; i++) {
            boolean cap = i == 0 || Math.random() < 0.1f;
            boolean comma = !cap && Math.random() < 0.2f;

            String append = text.get(i);
            if (cap) {
                append = (i == 0 ? "" : ".\n") + Character.toUpperCase(append.charAt(0)) + append.substring(1);
            }
            if (comma) {
                append = ", " + append;
            }
            if (!comma && !cap) {
                append = " " + append;
            }

            built.append(append);
        }
        built.append('.');

        return built.toString();
    }


    protected void assertNonNull(String tag, Object a) {
        if (a != null)
            return;
        throw new AssertionError(tag + ": null");
    }

    protected <T extends Comparable<T>> void assertLessOrEquals(String tag, T a, T b) {
        if (a == null ? b == null : a.compareTo(b) <= 0)
            return;
        throw new AssertionError(tag + ": '" + String.valueOf(a) + "' > '" + String.valueOf(b) + "'");
    }

    protected <T extends Comparable<T>> void assertLess(String tag, T a, T b) {
        if (a == null ? b == null : a.compareTo(b) < 0)
            return;
        throw new AssertionError(tag + ": '" + String.valueOf(a) + "' >= '" + String.valueOf(b) + "'");
    }

    protected void assertEquals(String tag, Object a, Object b) {
        if (a == null ? b == null : a.equals(b))
            return;
        throw new AssertionError(tag + ": '" + String.valueOf(a) + "' != '" + String.valueOf(b) + "'");
    }

    protected <T> void assertEquals(String tag, T[] a, T[] b) {
        if (a == null ? b == null : Arrays.equals(a, b))
            return;
        throw new AssertionError(tag + ": '" + Arrays.toString(a) + "' != '" + Arrays.toString(b) + "'");
    }

    public abstract void test(AccessProfile profile);
    public CharSequence title() {
        return SU.camelCaseToStr(this.getClass().getSimpleName());
    }
}
