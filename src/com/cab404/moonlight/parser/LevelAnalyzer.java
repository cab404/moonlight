package com.cab404.moonlight.parser;

import com.cab404.moonlight.util.SU;
import com.cab404.moonlight.util.U;

import java.util.*;

/**
 * Довольно простой эвристический анализатор ошибок HTML.
 */
public class LevelAnalyzer {
    private List<LeveledTag> tags;
    private CharSequence linked;
    private BlockHandler handler;


    LevelAnalyzer(CharSequence text) {
        tags = new ArrayList<>();
        linked = text;
    }

    public void add(Tag tag) {
        tag.index = tags.size();
        tags.add(new LeveledTag(tag, currentLevel()));

        if (tag.isClosing()) {
            LeveledTag opening;

            try {
                opening = findOpening(tag.index);
//                fixLyingLoners(tag.index , tags.size());

            } catch (RuntimeException e) {
                if (e.getMessage().contains("PAAANIIIIC!!!!")) {
                    U.w("No opening for " + tag);
                    return;
                } else
                    throw e;
            }

            if (handler != null) {
                BlockBuilder blockBuilder = new BlockBuilder();
                blockBuilder.header = opening.tag;
                blockBuilder.footer = tag;
                handler.handleBlock(blockBuilder);
            }
        }

    }

    public LeveledTag get(int index) {
        return tags.get(index);
    }
    public int size() {return tags.size();}

    public void setBlockHandler(BlockHandler handler) {
        this.handler = handler;
    }

    public class LeveledTag {
        public final Tag tag;
        private int level;
        private boolean fixed = false;

        public int getLevel() {
            return level;
        }

        private LeveledTag(Tag tag, int level) {
            this.tag = tag;
            this.level = level;
        }
    }


    /**
     * Анализируем и чиним всё, что можем не добавляя ничего нового.
     */
    private Map<String, Integer> analyzeSlice(int start, int end) {
        HashMap<String, Integer> levels = new HashMap<>();

        for (int i = start; i < end; i++) {
            LeveledTag checking = tags.get(i);
            if (checking.tag.isComment()) continue;
            if (checking.fixed) continue;

            int c_level;

            if (!levels.containsKey(checking.tag.name)) {
                levels.put(checking.tag.name, 0);
                c_level = 0;
            } else
                c_level = levels.get(checking.tag.name);

            if (checking.tag.isClosing())
                if (c_level == 0) checking.tag.type = Tag.Type.STANDALONE;
                else c_level--;

            if (checking.tag.isOpening())
                c_level++;

            levels.put(checking.tag.name, c_level);

        }

        levels = new HashMap<>();

        for (int i = end - 1; i >= start; i--) {
            LeveledTag checking = tags.get(i);
            if (checking.tag.isComment()) continue;
            if (checking.fixed) continue;

            int c_level;

            if (!levels.containsKey(checking.tag.name)) {
                levels.put(checking.tag.name, 0);
                c_level = 0;
            } else c_level = levels.get(checking.tag.name);


            if (checking.tag.isClosing())
                c_level--;

            if (checking.tag.isOpening())
                if (c_level == 0) checking.tag.type = Tag.Type.STANDALONE;
                else c_level++;

            levels.put(checking.tag.name, c_level);
        }

        return levels;
    }

    /**
     * Делаем из незакрытых тегов теги standalone (<tag/>)
     */
    private void fixLyingLoners(int start, int end) {
        Map<String, Integer> levels = analyzeSlice(start, end);

        for (Map.Entry<String, Integer> e : levels.entrySet())
            if (e.getValue() != 0)
                throw new RuntimeException("Parsing error - cannot resolve tree at tag " + e.getKey());

    }

    public LeveledTag findOpening(int index) {
        LeveledTag end = tags.get(tags.size() - 1);
        int c_level = 0;
        for (int i = index; i >= 0; i--) {
            LeveledTag curr = get(i);
            if (curr.tag.name.equals(end.tag.name)) {
                if (curr.tag.isOpening()) {
                    c_level++;
                    if (c_level == 0)
                        return curr;
                }
                if (curr.tag.isStandalone()) {
                    if (c_level == -1) {
                        curr.tag.type = Tag.Type.OPENING;
                        return curr;
                    }
                }

                if (curr.tag.isClosing())
                    c_level--;

            }
        }

        throw new RuntimeException("WARNING, OPENING TAG NOT FOUND, ABORTING EVERYTHING! PAAANIIIIC!!!!");
    }

    void fixIndents(int start, int end) {
        int layer = 0;

        for (int i = start; i < end; i++) {
            LeveledTag curr = get(i);

            if (curr.tag.isClosing())
                layer--;

            curr.level = layer;

            if (curr.tag.isOpening())
                layer++;

        }
    }

    /**
     * Перерасставляет отступы.
     */
    public void fixLayout() {

        fixIndents(0, tags.size());
    }

    private int currentLevel() {
        if (tags.isEmpty()) return 0;
        else {
            LeveledTag last = tags.get(tags.size() - 1);
            if (last.tag.isOpening())
                return last.level + 1;
            else if (last.tag.isClosing())
                return last.level - 1;
            else
                return last.level;
        }
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        for (LeveledTag tag : tags) {
            builder
                    .append(tag.level)
                    .append(SU.tabs(tag.level))
                    .append(tag.tag)
                    .append("\n");
        }
        return builder.toString();
    }

    public List<LeveledTag> getSlice(int start, int end) {
        return Collections.unmodifiableList(tags.subList(start, end));
    }

    public static interface BlockHandler {
        public void handleBlock(BlockBuilder builder);
    }

    public class BlockBuilder {
        Tag header, footer;
        private HTMLTree built;

        public Tag getHeaderTag() {
            return header;
        }

        public HTMLTree assembleTree() {
            fixLyingLoners(header.index, footer.index + 1);
            fixIndents(header.index, footer.index + 1);
            List<LeveledTag> slice = getSlice(header.index, footer.index + 1);

            built = built == null ? new HTMLTree(slice, linked) : built;
            return built;
        }

    }

}
