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
 * Сборщик пакетов. Мненорм.
 *
 * @author cab404
 */
public class RequestFactory {
    private final HttpRequestBase request;
    private AccessProfile host;

    private RequestFactory(HttpRequestBase packet) {
        request = packet;
        request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    }

    /**
     * Возвращаяет собранный запрос.
     */
    public HttpRequestBase build() {
        return request;
    }

    /**
     * Создаёт новый сборщик запроса типа GET
     */
    public static RequestFactory get(String rel_uri, AccessProfile profile) {
        HttpGet get = new HttpGet(rel_uri);

        RequestFactory factory = new RequestFactory(get);
        factory.host = profile;
        factory.addStandardHeaders();
        return factory;
    }


    /**
     * Создаёт новый сборщик запроса типа POST
     */
    public static RequestFactory post(String rel_uri, AccessProfile profile) {
        HttpPost post = new HttpPost(rel_uri);

        RequestFactory factory = new RequestFactory(post);
        factory.host = profile;
        factory.addStandardHeaders();
        return factory;
    }

    public RequestFactory setBody(String body) {
        return setBody(body, false);
    }

    /**
     * Присваивает телу запроса данную строку в кодировке UTF-8
     */
    public RequestFactory setBody(String body, boolean chunked) {

        if (request instanceof HttpPost)
            ((HttpPost) request).setEntity(new StringEntity(body, chunked));
        else
            throw new UnsupportedOperationException("Нельзя использовать этот метод на не-post пакетах!");

        return this;
    }

    /**
     * Создаёт body нарезанного реквеста из множества ключ,значение,ключ,значение...
     */
    public RequestFactory MultipartRequest(EntrySet<String, String> body, boolean isChunked) {
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
     * Выставляет стандартые для любого пакета, идущего к Табуну, заголовки.
     * Без них обычно, Табун будет ругаться "Hacking attempt!"
     */
    private RequestFactory addStandardHeaders() {
        request.addHeader("Host", host.getHost().getHostName());
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Accept-Encoding", "gzip");
        request.addHeader("User-Agent", host.userAgentName());
        request.addHeader("Accept", "*/*");

        return this;
    }

    /**
     * Выставляет страницу, с которой был послан запрос.
     * К примеру, мы пытаемся залогинится с ключом со
     * страницы /404/, то сюда мы должны прописать,
     * что ключ взят со страницы /404/.
     */
    public RequestFactory addReferer(String referer) {

        request.addHeader("Referer", "http://" + host.getHost().getHostName() + referer);

        return this;
    }

    /**
     * Просто добавляет заголовок к запросу.
     */
    public RequestFactory addHeader(String name, String value) {
        request.addHeader(name, value);

        return this;
    }

    /**
     * Выставляет тип запроса и тип тела для XMLHttp-запроса.
     */
    public RequestFactory XMLRequest() {
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
