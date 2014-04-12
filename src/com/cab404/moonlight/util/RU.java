package com.cab404.moonlight.util;

import com.cab404.moonlight.framework.AccessProfile;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;

/**
 * Request utils.
 *
 * @author cab404
 */
public class RU {

    public static HttpResponse exec(HttpRequestBase request, AccessProfile profile, boolean follow, int timeout) {
        try {
            HttpClient client = new DefaultHttpClient();

            client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
            client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
            client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, follow);

            request.addHeader(profile.getCookies());

            HttpResponse response = client.execute(profile.getHost(), request);

            profile.handleCookies(response.getHeaders("Set-Cookie"));

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse exec(HttpRequestBase request, AccessProfile accessProfile, boolean follow) {
        return exec(request, accessProfile, follow, 60000);
    }

    public static HttpResponse exec(HttpRequestBase request, AccessProfile accessProfile) {
        return exec(request, accessProfile, true);
    }

    public static HttpResponse exec(HttpRequestBase request) {
        return exec(request, null);
    }



}
