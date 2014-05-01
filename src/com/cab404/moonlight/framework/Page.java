package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.RequestBuilder;
import com.cab404.moonlight.parser.HTMLAnalyzerThread;
import com.cab404.moonlight.parser.HTMLTagParserThread;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Represents web->raw-data step, and wrapper for raw-data->data.
 *
 * @author cab404
 */
public abstract class Page extends Request implements ModularBlockParser.ParsedObjectHandler {
    private HTMLAnalyzerThread content;
    private HTMLTagParserThread parser;
    private ModularBlockParser modules;

    {
        parser = new HTMLTagParserThread();
        content = new HTMLAnalyzerThread(parser.getHTML());
        parser.bondWithAnalyzer(content);
    }

    /**
     * Returns page url.
     */
    protected abstract String getURL();
    /**
     * Binds modules.
     * Binded module will receive blocks and it's output will be piped to ${link ModularBlockParser.ParsedObjectHandler#handle}
     */
    protected abstract void bindParsers(ModularBlockParser base);

    @Override protected void prepare(AccessProfile profile) {
        modules = new ModularBlockParser(this, profile);
        content.setBlockHandler(modules);

        bindParsers(modules);

        content.start();
    }

    @Override protected HttpRequestBase getRequest(AccessProfile profile) {
        String url = getURL();
        content.setName(url + " analyzer thread.");
        parser.setName(url + " parser thread.");
        return RequestBuilder.get(url, profile).build();
    }

    @Override public boolean line(String line) {
        return parser.line(line) && !modules.isEmpty();
    }

    @Override public void finished() {
        parser.finished();
    }

    @Override public void fetch(AccessProfile accessProfile) {
        super.fetch(accessProfile);

        parser.run();
        try {
            content.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}
