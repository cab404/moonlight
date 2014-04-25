package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class LoadingFail extends MoonlightException {
    public LoadingFail() {
    }
    public LoadingFail(String message) {
        super(message);
    }
    public LoadingFail(String message, Throwable cause) {
        super(message, cause);
    }
    public LoadingFail(Throwable cause) {
        super(cause);
    }
    public LoadingFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
