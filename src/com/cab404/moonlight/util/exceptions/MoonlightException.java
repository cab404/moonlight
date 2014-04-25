package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class MoonlightException extends RuntimeException {
    public MoonlightException() {
    }
    public MoonlightException(String message) {
        super(message);
    }
    public MoonlightException(String message, Throwable cause) {
        super(message, cause);
    }
    public MoonlightException(Throwable cause) {
        super(cause);
    }
    public MoonlightException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
