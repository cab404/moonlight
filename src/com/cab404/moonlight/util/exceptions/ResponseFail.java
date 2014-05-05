package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class ResponseFail extends MoonlightFail {
    public ResponseFail() {
    }
    public ResponseFail(String message) {
        super(message);
    }
    public ResponseFail(String message, Throwable cause) {
        super(message, cause);
    }
    public ResponseFail(Throwable cause) {
        super(cause);
    }
    public ResponseFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
