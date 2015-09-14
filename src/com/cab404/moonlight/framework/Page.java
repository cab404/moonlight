package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.RequestBuilder;
import com.cab404.moonlight.parser.MultiThread;
import com.cab404.moonlight.parser.ParsingThreadPolicy;
import com.cab404.moonlight.parser.SingleThread;
import org.apache.http.client.methods.HttpRequestBase;

import java.nio.CharBuffer;

/**
 * Represents web->raw-data step, and wrapper for raw-data->data.
 *
 * @author cab404
 */
public abstract class Page extends Request implements ModularBlockParser.ParsedObjectHandler {
    private ModularBlockParser modules;
    private ParsingThreadPolicy policy;
    private ModularBlockParser.ParsedObjectHandler handler;

    {
        setMultithreadMode(true);
    }

    public void setMultithreadMode(boolean on) {
        if (on)
            policy = new MultiThread();
        else
            policy = new SingleThread();
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

    @Override
    protected void prepare(AccessProfile profile) {
        policy.start();

        ModularBlockParser.ParsedObjectHandler h = this;
        if (handler != null)
            h = new MergedHandler(this, handler);

        modules = new ModularBlockParser(h, profile);

        bindParsers(modules);

        policy.provider().setBlockHandler(modules);
    }

    @Override
    protected HttpRequestBase getRequest(AccessProfile profile) {
        String url = getURL();
        return RequestBuilder.get(url, profile).build();
    }

    @Override
    public boolean part(CharBuffer part) {
        return !isCancelled() && policy.reciever().part(part) && !modules.isEmpty();
    }

    @Override
    public void finished() {
        policy.finished();
    }

    @Override
    public void fetch(AccessProfile accessProfile) {
        super.fetch(accessProfile);
        policy.join();
    }

    public ModularBlockParser.ParsedObjectHandler getHandler() {
        return handler;
    }

    public void setHandler(ModularBlockParser.ParsedObjectHandler handler) {
        this.handler = handler;
    }

    class MergedHandler implements ModularBlockParser.ParsedObjectHandler {

        private final ModularBlockParser.ParsedObjectHandler h1;
        private final ModularBlockParser.ParsedObjectHandler h2;

        public MergedHandler(ModularBlockParser.ParsedObjectHandler h1, ModularBlockParser.ParsedObjectHandler h2) {
            this.h1 = h1;
            this.h2 = h2;
        }

        @Override
        public void handle(Object object, int key) {
            h1.handle(object, key);
            h2.handle(object, key);
        }
    }
}
