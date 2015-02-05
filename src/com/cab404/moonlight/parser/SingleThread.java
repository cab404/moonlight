package com.cab404.moonlight.parser;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.framework.BlockProvider;

/**
 * Sorry for no comments!
 * Created at 4:53 on 03.01.15
 *
 * @author cab404
 */
public class SingleThread implements ParsingThreadPolicy {
    InlinedBlockParser parser = new InlinedBlockParser();

    @Override
    public void start() {
    }

    @Override
    public void join() {
        // Waiting...
        while (true)
            if (parser.isFinished())
                break;

    }

    @Override
    public void finished() {
        parser.finished();
    }

    @Override
    public BlockProvider provider() {
        return parser;
    }

    @Override
    public ResponseFactory.Parser reciever() {
        return parser;
    }
}
