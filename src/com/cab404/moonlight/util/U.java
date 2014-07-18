package com.cab404.moonlight.util;

import com.cab404.moonlight.util.logging.Logger;
import com.cab404.moonlight.util.logging.SystemOutLogger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * U is for the Utils
 * Куча статичных методов и переменных (по идее).
 *
 * @author cab404
 */
public class U {
    private static Logger logger = SystemOutLogger.getInstance();

    public static void setLogger(Logger logger) {
        U.logger = logger;
    }

    /**
     * Sends verbose message to current logger.
     */
    public static void v(Object obj) {
        logger.verbose(String.valueOf(obj));
    }

    /**
     * Sends warning to current logger.
     */
    public static void w(Object obj) {
        logger.error(obj == null ? null : obj.toString() + "\n");
    }

    /**
     * Sends throwable converted to string to current logger.
     */
    public static void w(Throwable obj) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        if (obj != null) obj.printStackTrace(out);
        logger.error(writer.toString() + "\n");
    }

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
