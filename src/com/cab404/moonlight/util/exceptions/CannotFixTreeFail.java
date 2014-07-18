package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class CannotFixTreeFail extends MoonlightFail {
    public CannotFixTreeFail() {
    }
    public CannotFixTreeFail(String message) {
        super(message);
    }
    public CannotFixTreeFail(String message, Throwable cause) {
        super(message, cause);
    }
    public CannotFixTreeFail(Throwable cause) {
        super(cause);
    }
    public CannotFixTreeFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
