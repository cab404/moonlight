package com.cab404.moonlight.facility;

import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.framework.EntrySet;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.CoreProtocolPNames;

import java.io.*;
import java.util.Map;
import java.util.UUID;

/**
 * Simple chain packet builder for Apache library.
 *
 * @author cab404
 */
public class RequestBuilder {
    private final HttpRequestBase request;
    private AccessProfile host;

    private RequestBuilder(HttpRequestBase packet) {
        request = packet;
        request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    }

    /**
     * Returns built packet.
     */
    public HttpRequestBase build() {
        return request;
    }

    /**
     * Creates GET packet builder.
     */
    public static RequestBuilder get(String rel_uri, AccessProfile profile) {
        HttpGet get = new HttpGet(rel_uri);

        RequestBuilder factory = new RequestBuilder(get);
        factory.host = profile;
        factory.addStandardHeaders();
        return factory;
    }


    /**
     * Creates POST packet builder
     */
    public static RequestBuilder post(String rel_uri, AccessProfile profile) {
        HttpPost post = new HttpPost(rel_uri);

        RequestBuilder factory = new RequestBuilder(post);
        factory.host = profile;
        factory.addStandardHeaders();
        return factory;
    }

    public RequestBuilder setBody(String body) {
        return setBody(body, false);
    }

    /**
     * Adds body to packet, encoded in UTF-8
     */
    public RequestBuilder setBody(String body, boolean chunked) {

        if (request instanceof HttpPost)
            ((HttpPost) request).setEntity(new StringEntity(body, chunked));
        else
            throw new UnsupportedOperationException("Cannot use on non-POST packets!");

        return this;
    }

    /**
     * Adds multipart/form-data body to packet.
     */
    public RequestBuilder MultipartRequest(EntrySet<String, String> body, boolean isChunked) {
        String boundary = UUID.randomUUID().toString().substring(24);
        request.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

        boundary = "--" + boundary;

        StringBuilder packet = new StringBuilder();
        for (Map.Entry<String, String> e : body)
            packet
                    .append(boundary).append("\r\n")
                    .append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"\r\n\r\n")
                    .append(e.getValue()).append("\r\n");

        packet.append(boundary).append("--\r\n");

        setBody(packet.toString(), isChunked);

        return this;
    }

    /**
     * Inserts standard headers.
     */
    private RequestBuilder addStandardHeaders() {
        request.addHeader("Host", host.getHost().getHostName());
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Accept-Encoding", "gzip");
        request.addHeader("User-Agent", host.userAgentName());
        request.addHeader("Accept", "*/*");

        return this;
    }

    /**
     * Sets referer.
     */
    public RequestBuilder addReferer(String referer) {

        request.addHeader("Referer", "http://" + host.getHost().getHostName() + referer);

        return this;
    }

    /**
     * Simply adds header to the packet.
     */
    public RequestBuilder addHeader(String name, String value) {
        request.addHeader(name, value);

        return this;
    }

    /**
     * Sets packet type to application/x-www-form-urlencoded, and adds X-Requested-With data.
     */
    public RequestBuilder XMLRequest() {
        request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        request.addHeader("X-Requested-With", "XMLHttpRequest");

        return this;
    }

    private class StringEntity implements HttpEntity {
        private final String str;
        private boolean chunked;

        private StringEntity(String str, boolean chunked) {
            this.str = str;
            this.chunked = chunked;
        }

        @Override public boolean isRepeatable() {
            return true;
        }

        @Override public boolean isChunked() {
            return chunked;
        }

        @Override public long getContentLength() {
            return str.length();
        }

        @Override public Header getContentType() {
            return null;
        }

        @Override public Header getContentEncoding() {
            return null;
        }

        @Override public InputStream getContent()
        throws IOException, IllegalStateException {
            return null;
        }

        @Override public void writeTo(OutputStream outputStream)
        throws IOException {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            writer.write(str);
            writer.close();
        }

        @Override public boolean isStreaming() {
            return false;
        }

        @Override public void consumeContent()
        throws IOException {

        }

    }

}
