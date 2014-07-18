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
import java.nio.CharBuffer;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

/**
 * Response reader.
 *
 * @author cab404
 */
public class ResponseFactory {

	private static final String BUFFER_SIZE_KEY = "com.cab404.moonlight.DEFAULT_CHARACTER_BUFFER_SIZE";

	private static int getBufferSize() {
		int def = 64;
		try {
			if (System.getProperty(BUFFER_SIZE_KEY) == null)
				throw new RuntimeException();
			return Integer.parseInt(System.getProperty(BUFFER_SIZE_KEY));
		} catch (Exception e) {
			System.setProperty(BUFFER_SIZE_KEY, def + "");
			return def;
		}
	}

	public static interface Parser {
		public boolean part(CharBuffer part);
		public void finished();
	}

	/**
	 * Fetches page and passes every line to parser
	 */
	public static void read(HttpResponse response, Parser parser) {
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(getRightInputStream(response)));
			CharBuffer buffer = CharBuffer.allocate(getBufferSize());
			while (reader.read(buffer) != 0 && parser.part((CharBuffer) buffer.flip())) ;
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

			long length = response.getEntity().getContentLength();
			if (length == -1) length = 1;
			long loaded = 0;

			CharBuffer buffer = CharBuffer.allocate(getBufferSize());

			while (reader.read(buffer) != 0) {
				buffer.flip();
				parser.part(buffer);
				loaded += buffer.limit();
				buffer.clear();
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

		final StringBuilder data = new StringBuilder();

		read(response, new Parser() {
			@Override public boolean part(CharBuffer part) {
				data.append(part);
				return true;
			}
			@Override public void finished() {

			}
		}, status);
		return data.toString();
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
