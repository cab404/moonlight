package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class NotFoundFail extends MoonlightFail {
    public NotFoundFail() {
    }
    public NotFoundFail(String message) {
        super(message);
    }
    public NotFoundFail(String message, Throwable cause) {
        super(message, cause);
    }
    public NotFoundFail(Throwable cause) {
        super(cause);
    }
    public NotFoundFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
