package com.cab404.moonlight.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Simple logging class.
 *
 * @author cab404
 */
public class Log {

    private static Logger logger = SystemOutLogger.getInstance();

    public static void setLogger(Logger logger) {
        Log.logger = logger;
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
        logger.error(obj == null ? null : obj.toString());
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

}
