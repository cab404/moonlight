package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class MoonlightFail extends RuntimeException {
    public MoonlightFail() {
    }
    public MoonlightFail(String message) {
        super(message);
    }
    public MoonlightFail(String message, Throwable cause) {
        super(message, cause);
    }
    public MoonlightFail(Throwable cause) {
        super(cause);
    }
    public MoonlightFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
