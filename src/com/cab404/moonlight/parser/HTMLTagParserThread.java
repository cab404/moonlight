package com.cab404.moonlight.parser;

import com.cab404.moonlight.facility.ResponseFactory;

import java.nio.CharBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Downloads and parses text to *ML nodes.
 * Works in pair with
 *
 * @author cab404
 * @see com.cab404.moonlight.parser.HTMLAnalyzerThread
 */
public class HTMLTagParserThread extends Thread implements ResponseFactory.Parser {
    private final Queue<String> queue;
    private HTMLAnalyzerThread bonded_analyzer;
    private TagParser parser;
    private boolean finished = false;

    Throwable exception = null;

    public CharSequence getHTML() {
        return parser.getHTML();
    }

    /**
     * Connects analyzer to this thread.
     */
    public void bondWithAnalyzer(HTMLAnalyzerThread bonded_analyzer) {
        this.bonded_analyzer = bonded_analyzer;
        parser.setTagHandler(bonded_analyzer);
    }

    public HTMLTagParserThread() {
        queue = new ConcurrentLinkedQueue<>();
        parser = new TagParser();
        setDaemon(true);
    }

    @Override
    public void run() {
        try {

            while ((!finished || !queue.isEmpty()) && !Thread.interrupted())
                while (!queue.isEmpty())
                    parser.process(queue.poll());

            bonded_analyzer.finished();

        } catch (Throwable t) {
            exception = t;
            finished = true;
        }
    }

    @Override
    public synchronized boolean part(CharBuffer part) {

        queue.add(part.toString());
        return true;

    }

    @Override
    public synchronized void finished() {
        finished = true;
    }
}
