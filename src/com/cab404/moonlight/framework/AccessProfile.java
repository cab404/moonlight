package com.cab404.moonlight.framework;

import com.cab404.moonlight.util.SU;
import com.cab404.moonlight.util.U;
import com.cab404.moonlight.util.exceptions.ResponseFail;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles and stores cookies. Contains hostname.
 * No expiration date handling provided for cookies.
 *
 * @author cab404
 */
public class AccessProfile {
    public HashMap<String, String> cookies = new HashMap<>();
    protected HttpHost host;

    /**
     * Returns host for sending requests.
     */
    public HttpHost getHost() {
        return host;
    }

    /**
     * Returns UserAgent header value.
     */
    public String userAgentName() {
        return "Moonlight";
    }

    /**
     * Creates AccessProfile from hostname using port 80
     */
    public AccessProfile(String host) {
        this(host, 80);
    }

    protected AccessProfile() {}

    /**
     * Creates AccessProfile from hostname and port
     */
    public AccessProfile(String host, int port) {
        this.host = new HttpHost(host, port);
    }

    /**
     * Builds a Cookie header out of stored cookies.
     */
    public synchronized Header getCookies() {
        String out = "";

        for (Map.Entry<String, String> cookie : cookies.entrySet())
            out += cookie.getKey() + "=" + cookie.getValue() + "; ";

        return new BasicHeader("Cookie", out);
    }

    /**
     * Searches for "Cookie" headers and saves their values to storage
     */
    public synchronized void handleCookies(Header[] headers) {
        for (Header header : headers)
            addCookies(header.getValue());
    }

    /**
     * Takes value of "Cookie" header and saves it in map, replacing older ones if needed.
     */
    protected void addCookies(String input) {
        List<String> cookenziii = SU.split(input, "; ");
        for (String coookiiie : cookenziii) {
            List<String> split = SU.split(coookiiie, "=");
            if (split.size() == 2)
                cookies.put(split.get(0), split.get(1));
        }
    }

    /**
     * Returns cookies.
     */
    @Override
    public String toString() {
        return getCookies().getValue();
    }


    /**
     * Serializes AccessProfile to string with format host:port@cookies.
     *
     * @see AccessProfile#setUpFromString(String)
     * @see AccessProfile#parseString(String)
     */
    public String serialize() {
        return getHost().getHostName() + ":" + getHost().getPort() + "@" + SU.rl(getCookies().getValue());
    }

    /**
     * Loads serialised data from string (see {@link AccessProfile#serialize()}) into this object.
     * Pretty useful when overriding parseString in subclasses.
     */
    protected void setUpFromString(String s) {
        List<String> name_and_everything_else = SU.split(s, ":", 2);
        List<String> port_and_cookies = SU.split(SU.drl(name_and_everything_else.get(1)), "@", 2);

        host = new HttpHost(name_and_everything_else.get(0), U.parseInt(port_and_cookies.get(0)));
        addCookies(port_and_cookies.get(1));
    }

    /**
     * Creates AccessProfile from serialized form.
     *
     * @see AccessProfile#serialize()
     */
    public static AccessProfile parseString(String s) {
        AccessProfile _return = new AccessProfile();
        _return.setUpFromString(s);
        return _return;
    }


    public HttpResponse exec(HttpRequestBase request, boolean follow, int timeout) {
        try {
            HttpClient client = new DefaultHttpClient();

            client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
            client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
            client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, follow);

            HttpResponse response;
            if (request.getURI().getHost() != null) {
                response = client.execute(request);
            } else {
                request.addHeader(getCookies());
                response = client.execute(getHost(), request);
            }
            handleCookies(response.getHeaders("Set-Cookie"));

            return response;
        } catch (IOException e) {
            throw new ResponseFail(e);
        }
    }

    public HttpResponse exec(HttpRequestBase request, boolean follow) {
        return exec(request, follow, 20000);
    }

    public HttpResponse exec(HttpRequestBase request) {
        return exec(request, true);
    }

}
