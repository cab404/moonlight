package com.cab404.moonlight.parser;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.framework.BlockProvider;
import com.cab404.moonlight.util.exceptions.ParseFail;
import com.cab404.moonlight.util.exceptions.StreamDecodeFail;

/**
 * Sorry for no comments!
 * Created at 4:57 on 03.01.15
 *
 * @author cab404
 */
public class MultiThread implements ParsingThreadPolicy {
    final HTMLTagParserThread parser;
    final HTMLAnalyzerThread analyzer;

    {
        parser = new HTMLTagParserThread();
        analyzer = new HTMLAnalyzerThread(parser.getHTML());
        parser.bondWithAnalyzer(analyzer);
    }

    @Override
    public void start() {
        parser.start();
        analyzer.start();
    }

    @Override
    public void join() {
        while (true)
            synchronized (analyzer) {
                try {
                    analyzer.wait();
                } catch (InterruptedException e) {
                    break;
                }
                if (analyzer.isThreadFinished())
                    break;
            }

        if (analyzer.exception != null)
            throw new StreamDecodeFail(analyzer.exception);
        if (parser.exception != null)
            throw new ParseFail(parser.exception);
    }

    @Override
    public void finished() {
        parser.finished();
    }

    @Override
    public BlockProvider provider() {
        return analyzer;
    }

    @Override
    public ResponseFactory.Parser reciever() {
        return parser;
    }
}
