package com.cab404.moonlight.framework;

import com.cab404.moonlight.facility.RequestBuilder;
import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.parser.HTMLAnalyzerThread;
import com.cab404.moonlight.parser.HTMLTagParserThread;
import com.cab404.moonlight.parser.InlinedBlockParser;
import org.apache.http.client.methods.HttpRequestBase;

import java.nio.CharBuffer;

/**
 * Represents web->raw-data step, and wrapper for raw-data->data.
 *
 * @author cab404
 */
public abstract class Page extends Request implements ModularBlockParser.ParsedObjectHandler {
	private BlockProvider provider;
	private ModularBlockParser modules;
	private ResponseFactory.Parser receiver;
	private Thread waitFor;

	{
		setMultithreadMode(false); // 'cause it sucks right now.
	}

	public void setMultithreadMode(boolean on) {
		if (on) {
			HTMLTagParserThread parser = new HTMLTagParserThread();
			HTMLAnalyzerThread analyzerThread = new HTMLAnalyzerThread(parser.getHTML());
			parser.bondWithAnalyzer(analyzerThread);

			waitFor = analyzerThread;
			provider = analyzerThread;
			receiver = parser;

		} else {
			InlinedBlockParser parser = new InlinedBlockParser();
			provider = parser;
			receiver = parser;
		}

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

		bindParsers(modules);

		provider.setBlockHandler(modules);
	}

	@Override protected HttpRequestBase getRequest(AccessProfile profile) {
		String url = getURL();
		return RequestBuilder.get(url, profile).build();
	}

	@Override public boolean part(CharBuffer part) {
		return !isCancelled() && receiver.part(part) && !modules.isEmpty();
	}

	@Override public void finished() {
		receiver.finished();
	}

	@Override public void fetch(AccessProfile accessProfile) {
		super.fetch(accessProfile);


		if (waitFor != null)
			try {
				waitFor.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
	}


}
