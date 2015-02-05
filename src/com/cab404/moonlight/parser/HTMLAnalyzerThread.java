package com.cab404.moonlight.parser;

import com.cab404.moonlight.framework.BlockProvider;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * HTML tree analyzing thread.
 *
 * @author cab404
 * @see com.cab404.moonlight.parser.HTMLTagParserThread
 */
public class HTMLAnalyzerThread extends Thread implements TagParser.TagHandler, BlockProvider {
    private final Queue<Tag> queue;
    private LevelAnalyzer analyzer;
    private boolean finished = false, threadFinished = false;

    public HTMLAnalyzerThread(CharSequence data) {
        queue = new ConcurrentLinkedQueue<>();
        this.analyzer = new LevelAnalyzer(data);
        setDaemon(true);
    }

    public void setBlockHandler(LevelAnalyzer.BlockHandler handler) {
        analyzer.setBlockHandler(handler);
    }

    @Override
    public void run() {

        while ((!finished || !queue.isEmpty()) && !Thread.interrupted())
            while (!queue.isEmpty()) {
                Tag tag = queue.poll();
                analyzer.add(tag);
            }

        synchronized (this) {
            this.notifyAll();
        }

        threadFinished = true;
    }

    @Override
    public synchronized void handle(Tag tag) {

        queue.add(tag);

    }

    public synchronized void finished() {
        finished = true;
    }

    public boolean isThreadFinished() {
        return threadFinished;
    }
}
