package com.cab404.moonlight.util;

import com.cab404.moonlight.facility.ResponseFactory;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * U is for the Utils
 * Куча статичных методов и переменных (по идее).
 *
 * @author cab404
 */
public class U {
    private static HashMap<Thread, Timer> timers;

    /**
     * Возвращает таймер для текущего потока.
     */
    public static Timer timer() {
        if (timers == null) timers = new HashMap<>();
        Timer timer = timers.get(Thread.currentThread());
        if (timer == null) {
            timer = new Timer();
            timers.put(Thread.currentThread(), timer);
        }
        return timer;
    }

    /**
     * Делает абсолютно то же, что и Log.v("Luna Log", obj.toString()),
     * плюс проверяет на null.
     */
    public static void v(Object obj) {
        try {
            System.out.println(obj == null ? null : obj.toString());
            System.out.flush();
        } catch (NullPointerException e) {
            w(e);
        }
    }

    /**
     * Делает абсолютно то же, что и Log.w("Luna Log", obj.toString()),
     * плюс проверяет на null.
     */
    public static void w(Object obj) {
        System.err.println(obj == null ? null : obj.toString());
        System.err.flush();
    }

    /**
     * Делает абсолютно то же, что и Log.w("Luna Log", obj.toString()),
     * плюс проверяет на null.
     */
    public static void w(Throwable obj) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        if (obj != null) obj.printStackTrace(out);
        System.err.println(writer.toString());
        System.err.flush();
    }

    public static void v(HttpRequestBase request) {
        v(request.getRequestLine());
        for (Header header : request.getAllHeaders())
            v(header.getName() + ": " + header.getValue());
        v("");
    }

    /**
     * Делает абсолютно то же, что и Log.wtf("Luna Log", obj.toString()),
     * плюс проверяет на null.
     */
    public static void wtf(Object obj) {
        System.err.println("WTF? " + (obj == null ? null : obj.toString()));
        System.err.flush();
    }

    /**
     * Достаёт рандомный элемент из массива. И всё :D
     */
    public static <T> T getRandomEntry(T[] values) {
        return values[(int) Math.floor(Math.random() * values.length)];
    }

    /**
     * Андроид, почему ты не любишь плюсы?
     */
    public static int parseInt(String in) {
        return Integer.parseInt(in.replace("+", ""));
    }

    public static float parseFloat(String in) {
        return Float.parseFloat(in.replace("+", ""));
    }


    public static Calendar convertDatetime(String datetime) {
        String timezone = datetime.substring(18);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));

        int year = Integer.parseInt(datetime.substring(0, 4));
        int month = Integer.parseInt(datetime.substring(5, 7)) - 1;
        int day = Integer.parseInt(datetime.substring(8, 10));

        int hour = Integer.parseInt(datetime.substring(11, 13));
        int minute = Integer.parseInt(datetime.substring(14, 16));
        int second = Integer.parseInt(datetime.substring(17, 19));

        //noinspection MagicConstant
        calendar.set(year, month, day, hour, minute, second);
        return calendar;
    }

    public static abstract class TextPartParser implements ResponseFactory.Parser {


        boolean started;
        StringBuilder builder;

        public TextPartParser() {
            builder = new StringBuilder();
        }

        @Override public boolean line(String line) {

            if (!started)
                started = isStart(line);
            if (!started)
                return true;

            builder.append(line).append("\n");

            if (isEnd(line)) {
                process(builder);
                return false;
            }

            return true;
        }

        @Override public void finished() {}

        public abstract void process(StringBuilder out);
        public abstract boolean isStart(String str);
        public abstract boolean isEnd(String str);


    }

    /**
     * Simple estimated time logger.
     */
    public static class Timer {
        long time;

        public Timer() {
            set();
        }

        /**
         * Starts the timer.
         */
        public void set() {
            time = System.nanoTime();
        }

        /**
         * Returns time from previous {@link com.cab404.moonlight.util.U.Timer#set} invoke in nanoseconds.
         */
        public long get() {
            return System.nanoTime() - time;
        }

        /**
         * Returns time from previous {@link com.cab404.moonlight.util.U.Timer#set} invoke in milliseconds.
         */
        public long getMs() {
            return (System.nanoTime() - time) / 1000000;
        }

        /** */
        public void log(String tag) {
            U.v(tag.replace(":time:", getMs() + " ms"));
        }

        public void logNano(String tag) {
            U.v(tag.replace(":time:", get() + " ns"));
        }

    }
}
