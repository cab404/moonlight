package com.cab404.moonlight.util.loaders;

import com.cab404.moonlight.facility.RequestFactory;
import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.util.html_parser.HTMLAnalyzerThread;
import com.cab404.moonlight.util.html_parser.HTMLTagParserThread;
import com.cab404.moonlight.util.html_parser.LevelAnalyzer;
import com.cab404.moonlight.util.html_parser.TagParser;
import com.cab404.moonlight.util.modular.AccessProfile;
import com.cab404.moonlight.util.modular.ModularBlockParser;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Represents web->raw-data step, and wrapper for raw-data->data.
 *
 * @author cab404
 */
public abstract class Page extends Request implements ModularBlockParser.ParsedObjectHandler {
    private HTMLAnalyzerThread content;
    private ModularBlockParser modules;

    /**
     * Возвращает url страницы.
     */
    public abstract String getURL();
    /**
     * Подключает парсеры в обработку тегов.
     */
    protected abstract void bindParsers(ModularBlockParser base);

    @Override public HttpRequestBase getRequest(AccessProfile accessProfile) {
        return RequestFactory.get(getURL(), accessProfile).build();
    }


    @Override public void response(ResponseFactory.Parser parser, AccessProfile profile) {
        TagParser tag_parser = null;
        LevelAnalyzer level_analyzer = null;

        while (tag_parser == null)
            tag_parser = ((HTMLTagParserThread) parser).getTagParser();

        while (level_analyzer == null)
            level_analyzer = content.getLevelAnalyzer();

        level_analyzer.fixLayout();

    }

    @Override public ResponseFactory.Parser getParser(AccessProfile profile) {
        HTMLTagParserThread parser = new HTMLTagParserThread();
        content = new HTMLAnalyzerThread(parser.getHTML());

        modules = new ModularBlockParser(this, profile);
        content.setBlockHandler(modules);
        bindParsers(modules);

        parser.setHandler(content);

        content.start();
        parser.start();

        content.setName(getURL() + " analyzer thread.");
        parser.setName(getURL() + " parser thread.");

        parser.bondWithAnalyzer(content);

        return parser;
    }

    @Override public void fetch(AccessProfile profile, ResponseFactory.StatusListener statusListener) {
        super.fetch(profile, statusListener);
    }
}
