package com.cab404.moonlight.util.modular;

import com.cab404.moonlight.util.SU;
import com.cab404.moonlight.util.U;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles and stores cookies.
 *
 * @author cab404
 */
public class AccessProfile {
    public HashMap<String, String> cookies;
    private HttpHost host;

    public HttpHost getHost() {
        return host;
    }

    public AccessProfile(String host) {
        this(host, 80);
    }

    public AccessProfile(String host, int port) {
        this.cookies = new HashMap<>();
        this.host = new HttpHost(host, port);
    }

    public synchronized Header getCookies() {
        String out = "";

        for (Map.Entry<String, String> cookie : cookies.entrySet())
            out += cookie.getKey() + "=" + cookie.getValue() + "; ";

        return new BasicHeader("Cookie", out);
    }

    public synchronized void handleCookies(Header[] headers) {
        for (Header header : headers)
            addCookies(header.getValue());
    }

    void addCookies(String input) {
        String cookie = input.split("; ")[0];
        String[] split = cookie.split("=");
        if (split.length == 2)
            cookies.put(split[0], split[1]);
    }

    @Override public String toString() {
        return getCookies().getValue();
    }

    public String serialize() {
        return getHost().getHostName() + ":" + getHost().getPort() + "@" + getCookies().getValue();
    }

    public static AccessProfile parseString(String s) {
        List<String> split = SU.split(s, ":", 2);
        List<String> split2 = SU.split(split.get(0), "@", 2);

        AccessProfile _return = new AccessProfile(split.get(0), U.parseInt(split2.get(0)));
        _return.addCookies(split2.get(1));

        return _return;
    }
}
