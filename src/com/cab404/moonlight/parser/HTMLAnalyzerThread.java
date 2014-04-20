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
    private final Object working_lock;
    public boolean started = false;


    public HTMLAnalyzerThread(CharSequence data) {
        queue = new CopyOnWriteArrayList<>();
        working_lock = new Object();
        this.analyzer = new LevelAnalyzer(data);
        setDaemon(true);
    }

    public void setBlockHandler(LevelAnalyzer.BlockHandler handler) {
        analyzer.setBlockHandler(handler);
    }

    @Override public void run() {
        synchronized (working_lock) {
            started = true;

            while (true) {

                if (!queue.isEmpty()) {
                    Tag tag = queue.remove(0);
                    if (tag == null)
                        break;
                    analyzer.add(tag);
                }

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
