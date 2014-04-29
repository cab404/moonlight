package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.util.RU;
import com.cab404.moonlight.util.exceptions.LoadingFail;
import com.cab404.moonlight.util.exceptions.RequestFail;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Base class for the requests.
 *
 * @author cab404
 */
public abstract class Request implements ResponseFactory.Parser {


    protected abstract HttpRequestBase getRequest(AccessProfile accessProfile);

    /**
     * Preparations before receiving data into {@link Request#line(String)}
     */
    protected void prepare(AccessProfile accessProfile) {}
    /**
     * It will receive data until we out of it or false is returned.
     */
    @Override public boolean line(String line) {return false;}
    /**
     * Invoked after data loading finished.
     */
    @Override public abstract void finished();

    /**
     * Do not process response entity in this method!
     * ...or throw error at least, so we won't activate parsers on empty stream.
     */
    protected void onResponseGain(HttpResponse response) {}

    protected void fetch(AccessProfile profile) {
        HttpRequestBase request = getRequest(profile);

        HttpResponse response;
        try {
            response = RU.exec(request, profile);
            onResponseGain(response);
        } catch (Throwable e) {
            throw new RequestFail(e);
        }

        prepare(profile);
        try {
            ResponseFactory.read(response, this);
        } catch (Throwable e) {
            throw new LoadingFail(e);
        }


    }

}
