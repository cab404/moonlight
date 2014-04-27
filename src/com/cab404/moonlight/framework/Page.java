package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.RequestFactory;
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
     * Возвращает url страницы.
     */
    protected abstract String getURL();
    /**
     * Подключает парсеры в обработку тегов.
     */
    protected abstract void bindParsers(ModularBlockParser base);

    @Override protected void prepare(AccessProfile profile) {
        modules = new ModularBlockParser(this, profile);
        content.setBlockHandler(modules);

        bindParsers(modules);

        content.start();
        parser.start();
    }

    @Override protected HttpRequestBase getRequest(AccessProfile profile) {
        String url = getURL();
        content.setName(url + " analyzer thread.");
        parser.setName(url + " parser thread.");
        return RequestFactory.get(url, profile).build();
    }

    @Override public boolean line(String line) {
        return parser.line(line) && !modules.isEmpty();
    }

    @Override public void finished() {
        parser.finished();
    }

    @Override public void fetch(AccessProfile accessProfile) {
        super.fetch(accessProfile);
        try {
            content.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
