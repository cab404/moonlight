package com.cab404.moonlight.parser;

import com.cab404.moonlight.parser.Tag.Type;
import com.cab404.moonlight.util.SU;

import java.util.List;

/**
 * Just a simple tag parser.
 *
 * @author cab404
 */
public class TagParser {
    private StringBuilder buffer;
    private TagHandler handler;
    private StringBuilder full_data;

    /**
     * It's StringBuilder, but still, do not change it's contents manually.
     * Please.
     * It can not only break all tags, produced by this parser, but if this
     * library happens to be it production, your colleagues may break some
     * of your bones too.
     */
    public CharSequence getHTML() {
        return full_data;
    }

    /**
     * Till this class doesn't actually needs any of tags it parses,
     * it will throw all of them to thing you'll stick in here.
     */
    public void setTagHandler(TagHandler handler) {
        this.handler = handler;
    }

    TagParser() {
        full_data = new StringBuilder();
        buffer = new StringBuilder();
    }

    private static final String
            COMM_START = "!--",
            COMM_END = "-->",
            TAG_START = "<",
            TAG_END = ">";

    int prev = 0;
    int i, j = 0;

    /**
     * Takes a chunk of text, appends it to buffer and full HTML, then tries to find some new tags.
     * <p/>
     * <br/>
     * <br/><strong>Q</strong>: Why haven't you created TagInputStream?
     * <br/><strong>A</strong>: IDK.
     * <br/>
     * <br/><strong>Q</strong>: Why are you using buffer AND full_data?
     * <br/><strong>A</strong>: I thought it would be faster and safer (and more fun for me)
     * to work with small buffer instead of full page. I might be terribly wrong.
     * <br/>
     * <br/><strong>Q</strong>: Can I feed it with raw bytes?
     * <br/><strong>A</strong>: I newer tried, but I suppose it will become sentient afterwards.
     */
    synchronized void process(String chunk) {
        buffer.append(chunk);
        full_data.append(chunk);

        while (true) {

            i = buffer.indexOf(TAG_START, 0);
            j = buffer.indexOf(TAG_END, i);
            if (i == -1 || j == -1) break;

            Tag tag = new Tag();
            tag.type = Type.OPENING;
            tag.start = prev + i;
            tag.end = prev + j + 1;
            tag.text = buffer.substring(i, j + 1);

            String inner = buffer.substring(i + 1, j);
            int l = inner.length() - 1;


            if (inner.startsWith(COMM_START)) {
                tag.type = Type.COMMENT;
                tag.name = COMM_START;

                j = buffer.indexOf(COMM_END, i);
                if (j == -1) break;
                j += COMM_START.length();
                tag.text = buffer.substring(i, j);
                j--;

                handler.handle(tag);
                step();
                continue;
            }


            if (inner.charAt(0) == '/') {
                tag.type = Type.CLOSING;
                inner = buffer.substring(i + 2, j);
            } else if (inner.charAt(l) == '/') {
                tag.type = Type.STANDALONE;
                inner = buffer.substring(i + 1, j - 1);
            }


            List<String> name_and_everything_else = SU.charSplit(inner, 2, ' ');
            tag.name = name_and_everything_else.get(0);

            if (tag.name.charAt(0) == '!')
                tag.type = Type.COMMENT; // Handling !doctype and others.

            if (name_and_everything_else.size() == 2) {
                // Parsing properties.
                String params = name_and_everything_else.get(1).trim();
                String key = null;

                // Current temporary position.
                int s = 0;

                // If we are parsing value in ' (true) or " (false) boundaries
                boolean quot = false;

                // 0 - searching for end of key,
                // 1 - searching for a start of value,
                // 2 - searching the end of value.
                int mode = 0;

                for (int index = 0; index < params.length(); index++) {
                    char current = params.charAt(index);
                    if (mode == 0 && current == '=') {
                        key = params.substring(s, index);
                        mode = 1;
                        continue;
                    }
                    if (mode == 1 && (current == '"' || current == '\'')) {
                        quot = current == '\'';
                        s = index + 1;
                        mode = 2;
                        continue;
                    }
                    if (mode == 2 && current == (quot ? '\'' : '"')) {
                        tag.props.put(key.trim(), params.substring(s, index));
                        s = index + 1;
                        mode = 0;
                    }

                }

            }

            step();
            handler.handle(tag);
        }
    }

    /**
     * Shrinks input buffer and moves counters to new positions.
     */
    private void step() {
        buffer.delete(0, j + 1);
        prev += j + 1;
    }

    static abstract interface TagHandler {
        public void handle(Tag tag);
    }

}
