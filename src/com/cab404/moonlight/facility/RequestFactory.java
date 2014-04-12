package com.cab404.moonlight.facility;

import com.cab404.moonlight.framework.AccessProfile;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;

import java.io.*;
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
        return setBody(body, true);
    }

    /**
     * Присваивает телу запроса данную строку в кодировке UTF-8
     */
    public RequestFactory setBody(String body, boolean isChunked) {
//        try {
        if (request instanceof HttpPost) {

            BasicHttpEntity entity = new BasicHttpEntity();
//
//                PipedInputStream in = new PipedInputStream();
//                PipedOutputStream out = new PipedOutputStream(in);
//                PrintWriter writer = new PrintWriter(out, true);
//
//                entity.setContent(in);
//                entity.setChunked(isChunked);
//                writer.write(body);
//                writer.close();

//                entity.setContentLength(body.length());
            ((HttpPost) request).setEntity(new StringEntity(body));

        } else {
            throw new UnsupportedOperationException("Нельзя использовать этот метод на не-post пакетах!");
        }
//        } catch (IOException ex) {
//            U.w(ex);
//        }

        return this;
    }

    /**
     * Создаёт body нарезанного реквеста из множества ключ,значение,ключ,значение...
     */
    public RequestFactory MultipartRequest(String... body) {
        String boundary = UUID.randomUUID().toString();
        request.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        String build_body = "";

        for (int i = 0; i != (body.length + 1) / 2; i++) {
            build_body += "--" + boundary + "\r\n";
            build_body += "Content-Disposition: form-data; title=\"" + body[i * 2] + "\"\r\n\r\n";
            build_body += body[i * 2 + 1] + "\r\n";
        }
        build_body += boundary + "--\r\n";

        setBody(build_body, true);

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
        request.addHeader("User-Agent", "sweetieBot");
        request.addHeader("Cookie", "");

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
     * Выставляет тип запроса и тип тела для типичного для Табуна, XMLHttp-запроса.
     */
    public RequestFactory XMLRequest() {
        request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        request.addHeader("X-Requested-With", "XMLHttpRequest");

        return this;
    }

    private static class StringEntity implements HttpEntity {
        private final String str;

        private StringEntity(String str) {
            this.str = str;
        }

        @Override public boolean isRepeatable() {
            return true;
        }

        @Override public boolean isChunked() {
            return false;
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
