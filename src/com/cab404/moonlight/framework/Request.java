package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.util.RU;
import com.cab404.moonlight.util.U;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * @author cab404
 */
public abstract class Request {


    public abstract HttpRequestBase getRequest(AccessProfile accessProfile);

    public abstract void response(ResponseFactory.Parser parser, AccessProfile profile);
    public abstract ResponseFactory.Parser getParser(AccessProfile profile);


    public void fetch(AccessProfile profile, ResponseFactory.StatusListener statusListener) {
        HttpRequestBase request = getRequest(profile);

        statusListener.onResponseStart();
        HttpResponse response;
        try {
            response = RU.exec(request, profile);
            int zone = response.getStatusLine().getStatusCode() / 100;
            if (zone > 3) {
                ErrorResponse resp = new ErrorResponse(response.getStatusLine().toString());
                resp.setStatusLine(response.getStatusLine());
                statusListener.onResponseFail(resp);
            }
        } catch (Throwable e) {
            U.w("Page: Response fail");
            U.w(e);
            statusListener.onResponseFail(e);
            return;
        }
//        U.v(response.getStatusLine());
        statusListener.onResponseFinished();

        statusListener.onLoadingStarted();
        ResponseFactory.Parser parser = getParser(profile);
        try {
            ResponseFactory.read(response, parser, statusListener);
        } catch (Throwable e) {
            U.w("Page: Loading fail");
            U.w(e);
            statusListener.onLoadingFail(e);
        }
        statusListener.onLoadingFinished();

        statusListener.onParseStarted();
        try {
            response(parser, profile);
        } catch (Throwable e) {
            U.w("Page: Parsing fail");
            U.w(e);
            statusListener.onParseFail(e);
            return;
        }
        statusListener.onParseFinished();

        statusListener.onFinish();

    }

    /**
     * Downloads and parses page.
     */
    public void fetch(AccessProfile accessProfile) {
        fetch(accessProfile, new ResponseFactory.StatusListener() { });
    }

    public static class ErrorResponse extends Exception {
        StatusLine error;
        public ErrorResponse(String s) {
            super();
        }
        private void setStatusLine(StatusLine line) {
            this.error = line;
        }
        public StatusLine getStatusLine() {
            return error;
        }
    }
}
