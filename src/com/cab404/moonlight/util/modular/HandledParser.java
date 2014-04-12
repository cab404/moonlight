package com.cab404.moonlight.util.modular;

import com.cab404.moonlight.facility.ResponseFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cab404
 */
public abstract class HandledParser implements ResponseFactory.Parser {
    private List<ParserHandler> handlers;

    public HandledParser() {
        handlers = new ArrayList<>();
    }

    public HandledParser(ParserHandler... handlers){
        this();
        this.handlers.addAll(Arrays.asList(handlers));
    }

    @SuppressWarnings("all")
    public void onFinish(){
        for (ParserHandler handler : handlers)
            handler.handle(this);
    }

}
