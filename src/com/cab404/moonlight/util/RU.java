package com.cab404.moonlight.util;

import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.util.exceptions.ResponseFail;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;

/**
 * Request utils. Moved into profile.
 *
 * @author cab404
 */
@Deprecated
public class RU {

    @Deprecated
    public static HttpResponse exec(HttpRequestBase request, AccessProfile profile, boolean follow, int timeout) {
        return profile.exec(request, follow, timeout);
    }

    @Deprecated
    public static HttpResponse exec(HttpRequestBase request, AccessProfile profile, boolean follow) {
        return profile.exec(request, follow);
    }

    @Deprecated
    public static HttpResponse exec(HttpRequestBase request, AccessProfile profile) {
        return profile.exec(request);
    }

    @Deprecated
    public static HttpResponse exec(HttpRequestBase request) {
        return new AccessProfile(null, 80).exec(request);
    }

}
