package com.cab404.moonlight.parser;

import com.cab404.moonlight.util.SU;
import com.cab404.moonlight.util.exceptions.NotFoundFail;

import java.util.*;

/**
 * Simple html navigation class
 *
 * @author cab404
 */
public class HTMLTree implements Iterable<Tag> {

    private final List<LevelAnalyzer.LeveledTag> leveled;
    public final CharSequence html;
    // used for logging
    private boolean subtree = false;

    @Override
    public Iterator<Tag> iterator() {
        return new Iterator<Tag>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public Tag next() {
                return get(i++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    /**
     * Returns index of first tag; basically, index offset of current tree. 
     */
    public int offset() {
        if (size() == 0)
            throw new RuntimeException("Zero-sized tree has no buffered_offset.");
        return leveled.get(0).tag.index;
    }


    public int size() {
        return leveled.size();
    }

    public Tag get(int index) {
        return leveled.get(index).tag;
    }

    public int indexOf(Tag tag) {
        return tag.index - offset();
    }

    public int getLevel(int index) {
        return leveled.get(index).getLevel();
    }

    public int getLevel(Tag tag) {
        return getLevel(tag.index - offset());
    }


    private HTMLTree(HTMLTree tree, int start, int end) {
        this.html = tree.html;
        this.subtree = true;
        this.leveled = tree.leveled.subList(start, end);
    }

    public HTMLTree(LevelAnalyzer analyzed, CharSequence data) {
        this(analyzed.getSlice(0, analyzed.size()), data, false);
    }

    public HTMLTree(List<LevelAnalyzer.LeveledTag> analyzed, CharSequence data, boolean isSubtree) {
        html = data;
        leveled = analyzed;
        subtree = isSubtree;
    }

    public HTMLTree(String text) {
        final LevelAnalyzer analyzer = new LevelAnalyzer(text);

        TagParser parser = new TagParser();

        parser.setTagHandler(new TagParser.TagHandler() {
            @Override
            public void handle(Tag tag) {
                analyzer.add(tag);
            }
        });

        parser.process(text);

        html = text;

        analyzer.fix();
        leveled = analyzer.getSlice(0, analyzer.size());

    }

    public Tag getTagByID(String id) {
        for (Tag tag : this)
            if (tag.props.containsKey("id")) {
                if (SU.fast_match(id, tag.get("id")))
                    return tag;
            }
        throw new TagNotFoundException();
    }

    private int getIndexForTag(Tag tag) {
        return tag.index - offset();
    }

    public int getClosingTag(Tag tag) {

        int index = getIndexForTag(tag) + 1, level = getLevel(tag);
        for (; index <= size(); index++) {

            Tag check = get(index);
            int c_level = getLevel(check);

            if (c_level == level && check.name.equals(tag.name))
                return getIndexForTag(check);

        }

        throw new TagNotFoundException();
    }

    public String getContents(int index) {
        return getContents(get(index));
    }

    public String getContents(Tag tag) {
        return html.subSequence(tag.end, get(getClosingTag(tag)).start).toString();
    }

    /**
     * Возвращает уровень тегов целиком.
     */
    public HTMLTree getTree(Tag opening) {

        if (opening.isClosing()) throw new NotFoundFail("You can't get subtree for closing tag.");
        if (opening.isStandalone()) throw new NotFoundFail("You can't get subtree for closing tag.");

        return new HTMLTree(this, opening.index - offset(), getClosingTag(opening) + 1);
    }

    /**
     * Возвращает уровень тегов целиком.
     */
    public HTMLTree getTree(int index) {
        return getTree(get(index));
    }

    public List<Tag> getTopChildren(Tag tag) {
        if (tag.isClosing()) throw new NotFoundFail("You can't get children of closing tag.");
        if (tag.isStandalone()) throw new NotFoundFail("You can't get children of standalone tag.");


        ArrayList<Tag> _return = new ArrayList<>();


        int index = getIndexForTag(tag) + 1, level = getLevel(tag);

        for (; index <= size(); index++) {

            Tag check = get(index);
            int c_level = getLevel(check);

            if (getLevel(check) - 1 == level)
                if (check.isOpening() || check.isStandalone())
                    _return.add(check);

            if (c_level == level)
                break;

        }

        return _return;
    }

    @Override
    public String toString(){
        return toString(true);
    }

    public String toString(boolean formatted) {
        if (size() == 0) return html.toString();

        int shift = getLevel(0);
        StringBuilder out;

        String lineSeparator= formatted ? "\n" : "";

        // If we are performing on subtree, then skipping pre-tag data.
        if (!subtree)
            out = new StringBuilder(
                    get(0).start > 0
                            ? (html.subSequence(0, get(0).start) + lineSeparator)
                            : ""
            );
        else
            out = new StringBuilder();

        int end = -1;
        for (Tag tag : this) {
            if (end != -1) {
                CharSequence text = html.subSequence(end, tag.start);
                if (formatted)
                    text = SU.trim(text);
                if (text.length() > 0)
                    out
                            .append(formatted ? SU.tabs(getLevel(tag) - shift + 1) : "")
                            .append(text)
                            .append(lineSeparator);
            }
            out
                    .append(formatted ? SU.tabs(getLevel(tag) - shift) : "")
                    .append(tag)
                    .append(lineSeparator);
            end = tag.end;
        }

        if (!subtree && get(size() - 1).end <= html.length())
            out.append(html.subSequence(get(size() - 1).end, html.length()));

        return out.toString();
    }

    public static class TagNotFoundException extends RuntimeException {
        public TagNotFoundException() {
            super();
        }
    }

    public List<Tag> copyList() {
        ArrayList<Tag> ret = new ArrayList<>();
        for (Tag tag : this)
            ret.add(tag);
        return ret;
    }

    /**
     * Sort-of-xpath.<br/>
     * It's using {@link com.cab404.moonlight.util.SU#fast_match(String, String) SU.fast_match()} for matching request parts.<br/>
     * Scheme of path segment: <code>[tag name regex]&[key]=[value regex]&=[index]</code>
     * Example of path: <br/> <code>div/a&href=*somesite.com/span&=3</code>
     */
    public List<Tag> xPath(String path) {

        List<Tag> results = copyList();
        int shift = getLevel(0);
        for (int i = 0; i < results.size(); ) {
            Tag tag = results.get(i);
            if (tag.isClosing() || tag.isComment() || getLevel(tag) - shift > 1)
                results.remove(i);
            else
                i++;
        }

        List<String> request = SU.charSplit(path, '/');

        for (int index = 0; index < request.size(); index++) {

            List<String> node = SU.charSplit(request.get(index), '&');

            String name = node.remove(0);
            for (int i = 0; i < results.size(); ) {
                if (!SU.fast_match(name, results.get(i).name)) {
                    results.remove(i);
                    continue;
                }
                i++;
            }

            for (String quiz : node) {
                List<String> property = SU.charSplit(quiz, 2, '=');

                String p_name = property.get(0);
                String p_val = property.get(1);

                int indexEnforcment = -1;
                if (p_name.length() == 0){
                    try {
                        indexEnforcment = Integer.parseInt(p_val);
                    } catch (NumberFormatException e){
                        throw new RuntimeException(path + " contains malformed index node - " + p_val);
                    }
                }

                int realIndex = 0;
                for (int i = 0; i < results.size(); realIndex++) {
                    Tag proc = results.get(i);
                    if (indexEnforcment != -1 ){
                        if (realIndex != indexEnforcment) {
                            results.remove(i);
                            continue;
                        }
                    } else {
                        if (!(proc.props.containsKey(p_name) && SU.fast_match(p_val, proc.get(p_name)))) {
                            results.remove(i);
                            continue;
                        }
                    }
                    i++;
                }

            }

            if (index < request.size() - 1) {
                ArrayList<Tag> top = new ArrayList<>();
                for (Tag tag : results)
                    if (tag.isOpening())
                        top.addAll(getTopChildren(tag));
                results = top;
            }

        }

        return results;
    }

    public Tag xPathUnique(String query) {
        TagMatcher matcher = new TagMatcher(query);
        for (Tag tag : this)
            if (matcher.matches(tag))
                return tag;
        return null;

    }

    public String xPathStr(String str) {
        try {
            return getContents(xPathFirstTag(str));
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }

    public Tag xPathFirstTag(String str) {
        try {
            return xPath(str).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}