package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.util.RU;
import com.cab404.moonlight.util.exceptions.LoadingFail;
import com.cab404.moonlight.util.exceptions.RequestFail;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * @author cab404
 */
public abstract class Request implements ResponseFactory.Parser {


    protected abstract HttpRequestBase getRequest(AccessProfile accessProfile);

    /**
     * Приготовления перед получением данных в {@link Request#line(String)}
     */
    protected void prepare(AccessProfile accessProfile) {}
    /**
     * Сюда будут поступать полученные из сети данные (построчно).
     * Если данные больше не нужны, верните false.
     */
    @Override public boolean line(String line) {return false;}
    /**
     * Выполняется после завершения приёма данных.
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
