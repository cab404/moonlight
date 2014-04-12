package com.cab404.moonlight.util.loaders;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.util.modular.AccessProfile;

/**
 * Реквест на какую-нибудь мелочь, которая не требует параллельного парсинга.
 *
 * @author cab404
 */
public abstract class ShortRequest extends Request {

    public abstract void handleResponse(String response);


    @Override public void response(ResponseFactory.Parser parser, AccessProfile profile) {
        handleResponse(((Stringifyer) parser).text.toString());
    }

    @Override public ResponseFactory.Parser getParser(AccessProfile profile) {
        return new Stringifyer();
    }

    private class Stringifyer implements ResponseFactory.Parser {
        StringBuilder text;

        private Stringifyer() {
            text = new StringBuilder();
        }

        @Override public boolean line(String line) {
            text.append(line).append("\n");
            return true;
        }

        @Override public void finished() {}
    }

}
