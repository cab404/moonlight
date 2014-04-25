package com.cab404.moonlight.parser;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Поток обработки древа тегов.
 *
 * @author cab404
 * @see com.cab404.moonlight.parser.HTMLTagParserThread
 */
public class HTMLAnalyzerThread extends Thread implements TagParser.TagHandler {
    private CopyOnWriteArrayList<Tag> queue;
    private LevelAnalyzer analyzer;

    public HTMLAnalyzerThread(CharSequence data) {
        queue = new CopyOnWriteArrayList<>();
        this.analyzer = new LevelAnalyzer(data);
        setDaemon(true);
    }

    public void setBlockHandler(LevelAnalyzer.BlockHandler handler) {
        analyzer.setBlockHandler(handler);
    }

    @Override public void run() {

        while (true) {

            if (!queue.isEmpty()) {
                Tag tag = queue.remove(0);
                if (tag == null)
                    break;
                analyzer.add(tag);
            }

        }

    }

    @Override public void handle(Tag tag) {
        queue.add(tag);
    }

    public void finished() {
        queue.add(null);
    }

}
