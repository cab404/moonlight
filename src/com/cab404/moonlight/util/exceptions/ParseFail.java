package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class ParseFail extends MoonlightFail {
    public ParseFail() {
    }
    public ParseFail(String message) {
        super(message);
    }
    public ParseFail(String message, Throwable cause) {
        super(message, cause);
    }
    public ParseFail(Throwable cause) {
        super(cause);
    }
    public ParseFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
