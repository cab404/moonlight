package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class RequestFail extends MoonlightException {
    public RequestFail() {
    }
    public RequestFail(String message) {
        super(message);
    }
    public RequestFail(String message, Throwable cause) {
        super(message, cause);
    }
    public RequestFail(Throwable cause) {
        super(cause);
    }
    public RequestFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
