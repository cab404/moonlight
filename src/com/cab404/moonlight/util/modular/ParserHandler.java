package com.cab404.moonlight.util.modular;

/**
 * @author cab404
 */
public interface ParserHandler<T extends HandledParser> {
    public void handle(T parser);
}
