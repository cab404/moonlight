package com.cab404.moonlight.util.html_parser;

import java.util.HashMap;
import java.util.Map;

/**
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

    // Тег типа </x>
    public boolean isClosing() {
        return type == Type.CLOSING;
    }

    // Тег типа <x/>
    public boolean isStandalone() {
        return type == Type.STANDALONE;
    }

    // Тег типа <!-- x --> и <! x>
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
        return props.containsKey(property) ? props.get(property) : null;
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
