package com.cab404.moonlight.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * *ML node
 *
 * @author cab404
 */
public class Tag {

    public static enum Type {
        STANDALONE, CLOSING, OPENING, COMMENT
    }

    public int index;
    public int start, end;
    public String name, text;
    public Type type;

    // </x>
    public boolean isClosing() {
        return type == Type.CLOSING;
    }

    // <x/>
    public boolean isStandalone() {
        return type == Type.STANDALONE;
    }

    // <!-- x --> & <! x>
    public boolean isComment() {
        return type == Type.COMMENT;
    }

    public boolean isOpening() {
        return type == Type.OPENING;
    }


    public Map<String, String> props;

    public Tag() {
        props = new HashMap<>();
    }

    public String get(String property) {
        return props.containsKey(property) ? props.get(property) : "";
    }

    @Override public String toString() {
        if (isComment()) return text;

        StringBuilder builder = new StringBuilder().append("<");

        if (isClosing()) builder.append("/");
        builder.append(name);

        for (Map.Entry<String, String> e : props.entrySet()) {
            builder.append(" ");
            builder.append(e.getKey()).append("=").append("\"").append(e.getValue()).append("\"");
        }

        if (isStandalone()) builder.append("/");
        builder.append(">");


        return builder.toString();
    }
}
