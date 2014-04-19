package com.cab404.moonlight.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * String utils.
 *
 * @author cab404
 */
public class SU {
    /**
     * Splits string without reqexes
     */
    public static List<String> split(String source, String not_a_regex) {
        ArrayList<String> out = new ArrayList<>();
        int i = 0;
        while (true) {
            int j = source.indexOf(not_a_regex, i);

            if (j == -1) {
                out.add(source.substring(i));
                break;
            }
            out.add(source.substring(i, j));
            i = j + not_a_regex.length();
        }
        return out;
    }
    /**
     * Splits string without reqexes
     */
    public static List<String> split(String source, String not_a_regex, int limit) {
        ArrayList<String> out = new ArrayList<>();
        int i = 0;
        while (true) {
            int j = source.indexOf(not_a_regex, i);

            if (j == -1 || out.size() + 1 == limit) {
                out.add(source.substring(i));
                break;
            }
            out.add(source.substring(i, j));
            i = j + not_a_regex.length();
        }
        return out;
    }


    /**
     * Returns true if check contains symbol ch
     */
    public static boolean contains(char[] check, char ch) {
        for (char curr : check)
            if (curr == ch) return true;
        return false;
    }

    /**
     * Splits string using any of chars from "chars"
     * For instance, call<br/>
     * charSplit("test test, test.test", " .,") <br/>
     * will return you [test, test, test, test]
     */
    public static List<String> charSplit(String source, char... chars) {
        ArrayList<String> out = new ArrayList<>();
        int last = 0;

        for (int i = 0; i < source.length(); i++) {
            if (contains(chars, source.charAt(i))) {
                out.add(source.substring(last, i));
                last = i + 1;
            }
        }

        out.add(source.substring(last));
        return out;
    }

    /**
     * Splits string using any of chars from "chars"
     * For instance, call<br/>
     * charSplit("test test, test.test", 2, " .,") <br/>
     * will return you ["test", "test, test.test"]
     */
    public static List<String> charSplit(String source, int limit, char... chars) {
        ArrayList<String> out = new ArrayList<>();
        int last = 0;

        for (int i = 0; i < source.length(); i++) {
            if (contains(chars, source.charAt(i))) {
                out.add(source.substring(last, i));
                last = i + 1;

                if (out.size() + 1 == limit)
                    break;

            }
        }

        out.add(source.substring(last));
        return out;
    }

    /**
     * String.substring, только с выбором начала и конца строки в виде строк
     */
    public static String sub(String source, String start, String end) {

        int sIndex = source.indexOf(start);
        if (sIndex == -1) {
            throw new RuntimeException("Error while parsing string " + source + ", no start position found.");
        }
        sIndex += start.length();

        int eIndex = source.indexOf(end, sIndex);
        if (eIndex == -1) {
            throw new RuntimeException("Error while parsing string " + source + ", no end position found.");
        }
        return source.substring(sIndex, eIndex);
    }
    /**
     * Backwards sub, делает то же, что и sub, только с конца строки.
     */
    public static String bsub(String source, String end, String start) {
        int sIndex = source.lastIndexOf(start);
        if (sIndex == -1) {
            throw new RuntimeException("Error while parsing string " + source + ", no start position found.");
        }

        int eIndex = source.lastIndexOf(end, sIndex);
        if (eIndex == -1) {
            throw new RuntimeException("Error while parsing string " + source + ", no end position found.");
        }
        return source.substring(eIndex + end.length(), sIndex);
    }
    /**
     * Всего лишь сокращение URLEncoder.encode()
     */
    public static String rl(String toConvert) {
        try {
            return URLEncoder.encode(toConvert, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            U.w(e);
            return null;
        }
    }
    /**
     * Всего лишь сокращение URLDecoder.decode()
     */
    public static String drl(String toConvert) {
        try {
            return URLDecoder.decode(toConvert, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            U.w(e);
            return null;
        }
    }
    public static String join(Collection<String> strings, String delimeter) {
        StringBuilder out = new StringBuilder();
        Iterator<String> iterator = strings.iterator();

        while (iterator.hasNext())
            out.append(iterator.next()).append(iterator.hasNext() ? delimeter : "");

        return out.toString();
    }

    private static String[][] html_entities = {
            {"&quot;", "\""},
            {"&rlm;", " ‏"},
            {"&amp;", "&"},
            {"&ndash;", "–"},
            {"&lt;", "<"},
            {"&mdash;", "—"},
            {"&gt;", ">"},
            {"&lsquo;", "‘"},
            {"&OElig;", "Œ"},
            {"&rsquo;", "’"},
            {"&oelig;", "œ"},
            {"&sbquo;", "‚"},
            {"&Scaron;", "Š"},
            {"&ldquo;", "“"},
            {"&scaron;", "š"},
            {"&rdquo;", "”"},
            {"&Yuml;", "Ÿ"},
            {"&bdquo;", "„"},
            {"&circ;", "ˆ"},
            {"&dagger;", "†"},
            {"&tilde;", "˜"},
            {"&Dagger;", "‡"},
            {"&ensp;", " "},
            {"&permil;", "‰"},
            {"&emsp;", " "},
            {"&lsaquo;", "‹"},
            {"&thinsp;", " "},
            {"&rsaquo;", "›"},
            {"&zwnj;", " "},
            {"&euro;", "€"},
            {"&zwj;", " "},
            {"&lrm;", " "},
            {"&#039;", "'"}
    };

    /**
     * Фух. Эта штука меняет все HTML 4.0 и 2.0 entity на нормальный текст.
     */
    public static String deEntity(String in) {
        StringBuilder data = new StringBuilder(in);
        for (String[] replace : html_entities) {
            String a = replace[0];
            String b = replace[1];

            int i;
            while ((i = data.indexOf(a)) != -1) {
                data.replace(i, i + a.length(), b);
            }

        }
        return data.toString();
    }

    /**
     * Works with patterns like asd*g, also is pretty fast.
     */
    public static boolean fast_match(String regex, String data) {
        List<String> strings = charSplit(regex, '*');

        if (strings.size() == 1)
            return data.equals(regex);

        if (!(data.startsWith(strings.get(0)) && data.endsWith(strings.get(strings.size() - 1))))
            return false;

        int s, f;

        for (String str : strings) {
            s = data.indexOf(str, 0);
            f = s + str.length();

            if (s == -1)
                return false;

            data = data.substring(0, s) + data.substring(f);
        }

        return true;
    }

    public static String removeAllTags(String toProcess) {
        int s;
        while ((s = toProcess.indexOf('<')) != -1) {
            int f = toProcess.indexOf('>', s);
            if (f == -1) break;
            toProcess = toProcess.substring(0, s) + toProcess.substring(f + 1);
        }
        return toProcess;
    }

    public static String tabs(int num) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < num; i++) tabs.append("\t");
        return tabs.toString();
    }

    public static String table(Object... entries) {
        StringBuilder line = new StringBuilder("|");

        int column = 10;
        FillType ft = FillType.CENTER;

        for (Object entry : entries) {
            if (entry instanceof Integer) {
                column = (Integer) entry;
            } else if (entry instanceof FillType) {
                ft = (FillType) entry;
            } else if (entry instanceof CharSequence) {
                line
                        .append(fillSpaces(entry.toString(), column, 0, ft))
                        .append("|");
            }
        }
        return line.toString();
    }


    public static enum FillType {
        RIGHT, LEFT, CENTER
    }
    public static String fillSpaces(String fill, int num, int offset, FillType type) {
        int left = 0, right = 0;
        num -= offset * 2;

        switch (type) {
            case RIGHT:
                right = offset;
                left = offset + num - fill.length();
                break;
            case LEFT:
                left = offset;
                right = offset + num - fill.length();
                break;
            case CENTER:
                right = (int) Math.floor((float) (num - fill.length()) / 2) + offset;
                left = (int) Math.ceil((float) (num - fill.length()) / 2) + offset;
                break;
        }

        return spaces(left) + fill + spaces(right);

    }

    public static String spaces(int num) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < num; i++)
            spaces.append(" ");
        return spaces.toString();
    }

    /**
     * Убирает подряд стоящие одинаковые символы.
     */
    public static CharSequence removeRecurringChars(String in, char remove) {
        StringBuilder modify = new StringBuilder(in);

        for (int i = 0; i < modify.length() - 1; ) {
            if (modify.charAt(i) == remove) {
                while ((i + 1 < modify.length() - 1) && modify.charAt(i + 1) == remove) {
                    modify.deleteCharAt(i);
                }
            }
            i++;
        }

        return modify;
    }

    public static CharSequence camelCaseToStr(CharSequence camelCase) {
        StringBuilder builder = new StringBuilder(camelCase);

        for (int i = 0; i < builder.length(); i++) {
            if (Character.isUpperCase(builder.charAt(i))) {
                builder.replace(i, i + 1, " " + Character.toLowerCase(builder.charAt(i)));
            }
        }

        if (builder.charAt(0) == ' ') builder.deleteCharAt(0);
        builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));

        return builder;
    }

    public static CharSequence trim(CharSequence seq) {
        if (seq.length() == 0) return seq;
        int start = 0;
        int end = seq.length() - 1;
        for (; start < seq.length(); start++) if (seq.charAt(start) != ' ') break;
        for (; end >= start; end--) if (seq.charAt(end) != ' ') break;
        return seq.subSequence(start, end);
    }

}
