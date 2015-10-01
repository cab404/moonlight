package com.cab404.moonlight.parser;

import com.cab404.moonlight.util.SU;

/**
 * Tag matcher, uses 'tag&name=val*e&containsParam' expressions
 *
 * @author cab404
 */
public class TagMatcher {
    String[] keys, values;
    String name;

    public TagMatcher(String quiz) {
        String[] parts = SU.splitToArray(quiz, '&');
        name = parts[0];
        keys = new String[parts.length - 1];
        values = new String[parts.length - 1];

        for (int i = 1; i < parts.length; i++) {
            String node = parts[i];
            String[] property = SU.splitToArray(node, '=');
            keys[i - 1] = property[0];
            values[i - 1] = (property.length == 1) ? null : property[1];
        }

    }

    boolean matches(Tag tag) {
        if (name.length() > 0)
            if (!SU.fast_match(name, tag.name))
                return false;

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (!tag.props.containsKey(key))
                return false;
            else if (values[i] != null && !SU.fast_match(values[i], tag.get(key)))
                return false;

        }
        return true;
    }
}
