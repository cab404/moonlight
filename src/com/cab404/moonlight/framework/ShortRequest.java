package com.cab404.moonlight.framework;

import java.nio.CharBuffer;

/**
 * Simple request for a short string.
 *
 * @author cab404
 */
public abstract class ShortRequest extends Request {

    protected abstract void handleResponse(String response);

    private StringBuilder data;

    {data = new StringBuilder();}

    @Override public boolean part(CharBuffer part) {
        data.append(part);
        return true;
    }

    @Override public void finished() {
        handleResponse(data.toString());
    }

}
