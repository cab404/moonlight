package com.cab404.moonlight.parser;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.framework.BlockProvider;

import java.nio.CharBuffer;

/**
 * For single thread parsing
 *
 * @author cab404
 */
public class InlinedBlockParser implements ResponseFactory.Parser, BlockProvider {

    TagParser parser;
    LevelAnalyzer analyzer;
    boolean isFinished = false;

    public void setBlockHandler(LevelAnalyzer.BlockHandler handler) {
        analyzer.setBlockHandler(handler);
    }

    public InlinedBlockParser() {
        parser = new TagParser();
        parser.setTagHandler(new TagParser.TagHandler() {
            @Override
            public void handle(Tag tag) {
                analyzer.add(tag);
            }
        });
        analyzer = new LevelAnalyzer(parser.getHTML());
    }

    @Override
    public boolean part(CharBuffer part) {
        parser.process(part.toString());
        return true;
    }

    @Override
    public void finished(){
        synchronized (this){
            notify();
        }
        isFinished = true;
    }

    public boolean isFinished() {
        return isFinished;
    }

}
