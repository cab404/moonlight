package com.cab404.moonlight.framework;

/**
 * Реквест на какую-нибудь мелочь, которая не требует параллельного парсинга.
 *
 * @author cab404
 */
public abstract class ShortRequest extends Request {

    protected abstract void handleResponse(String response);

    private StringBuilder data;

    {data = new StringBuilder();}

    @Override public boolean line(String line) {
        data.append(line);
        return true;
    }

    @Override public void finished() {
        handleResponse(data.toString());
    }

}
