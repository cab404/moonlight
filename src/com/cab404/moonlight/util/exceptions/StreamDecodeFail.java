package com.cab404.moonlight.util.exceptions;

/**
 * @author cab404
 */
public class StreamDecodeFail extends MoonlightFail {
    public StreamDecodeFail() {
    }
    public StreamDecodeFail(String message) {
        super(message);
    }
    public StreamDecodeFail(String message, Throwable cause) {
        super(message, cause);
    }
    public StreamDecodeFail(Throwable cause) {
        super(cause);
    }
    public StreamDecodeFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
