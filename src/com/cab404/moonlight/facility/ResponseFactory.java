package com.cab404.moonlight.facility;

import com.cab404.moonlight.util.exceptions.LoadingFail;
import com.cab404.moonlight.util.exceptions.ParseFail;
import com.cab404.moonlight.util.exceptions.ResponseFail;
import com.cab404.moonlight.util.exceptions.StreamDecodeFail;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

/**
 * Response reader.
 *
 * @author cab404
 */
public class ResponseFactory {

    public static interface Parser {
        public boolean line(String line);
        public void finished();
    }

    /**
     * Fetches page and passes every line to parser
     */
    public static void read(HttpResponse response, Parser parser) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(getRightInputStream(response)));
            String line;
            while ((line = reader.readLine()) != null && parser.line(line)) ;
            parser.finished();

        } catch (IOException e) {
            throw new LoadingFail(e);
        }
    }

    /**
     * Fetches page and passes every line to parser
     */
    public static void read(HttpResponse response, Parser parser, StatusListener status) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(getRightInputStream(response)));
            String line;

            long length = response.getEntity().getContentLength();
            if (length == -1) length = 1;
            long loaded = 0;

            while ((line = reader.readLine()) != null) {
                parser.line(line);
                loaded += line.length();
                status.onLoadingProgress(loaded, length);
            }
            parser.finished();

        } catch (IOException e) {
            throw new LoadingFail(e);
        }
    }

    /**
     * @see String read(HttpResponse, Parser)
     */
    public static String read(HttpResponse response, StatusListener status) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(getRightInputStream(response)));
            String line, page = "";

            long length = response.getEntity().getContentLength();
            if (length == -1) length = 1;
            long loaded = 0;

            while ((line = reader.readLine()) != null) {
                page += line + "\n";
                loaded += line.length() + 1;
                status.onLoadingProgress(loaded, length);
            }

            return page;

        } catch (IOException e) {
            throw new LoadingFail(e);
        }
    }

    /**
     * @see String read(HttpResponse, Parser)
     */
    public static String read(HttpResponse response) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(getRightInputStream(response)));
            String line, page = "";

            while ((line = reader.readLine()) != null)
                page += line + "\n";

            return page;

        } catch (IOException e) {
            throw new LoadingFail(e);
        }
    }

    /**
     * Слушалка всяких там событий.
     */
    public static abstract class StatusListener {

        public void onResponseStart() {}
        public void onResponseFail(Throwable t) {
            throw new ResponseFail(t);
        }
        public void onResponseFinished() {}

        public void onLoadingStarted() {}
        public void onLoadingFail(Throwable t) {
            throw new LoadingFail(t);
        }
        public void onLoadingProgress(long loaded, long length) {}
        public void onLoadingFinished() {}

        public void onParseStarted() {}
        public void onParseFail(Throwable t) {
            throw new ParseFail(t);
        }
        public void onParseFinished() {}

        public void onFinish() {}

    }

    /**
     * Достаёт из ответа поток, и завёртывает его в нужный разпаковывающий поток (если нужно).
     */
    private static InputStream getRightInputStream(HttpResponse response) {
        try {
            if (response.containsHeader("Content-Encoding")) {
                String encoding = response.getFirstHeader("Content-Encoding").getValue();
                if ("gzip".equals(encoding))
                    return new GZIPInputStream(response.getEntity().getContent());
                if ("deflate".equals(encoding))
                    return new DeflaterInputStream(response.getEntity().getContent());
            } else
                return response.getEntity().getContent();

        } catch (IOException e) {
            throw new StreamDecodeFail(e);
        }

        return null;
    }


}
