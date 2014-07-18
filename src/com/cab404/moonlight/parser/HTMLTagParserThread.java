package com.cab404.moonlight.parser;

import com.cab404.moonlight.facility.ResponseFactory;

import java.nio.CharBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Downloads and parses text to *ML nodes.
 * Works in pair with
 *
 * @author cab404
 * @see com.cab404.moonlight.parser.HTMLAnalyzerThread
 */
public class HTMLTagParserThread extends Thread implements ResponseFactory.Parser {
	private HTMLAnalyzerThread bonded_analyzer;
	private CopyOnWriteArrayList<String> queue;
	private TagParser parser;
	private boolean started = false;

	public CharSequence getHTML() {
		return parser.getHTML();
	}

	/**
	 * Подключает анализатор к потоку тегов и посылает null в теги после завершения парсирования.
	 */
	public void bondWithAnalyzer(HTMLAnalyzerThread bonded_analyzer) {
		this.bonded_analyzer = bonded_analyzer;
		parser.setTagHandler(bonded_analyzer);
	}

	public HTMLTagParserThread() {
		this.queue = new CopyOnWriteArrayList<>();
		parser = new TagParser();
		setDaemon(true);
	}

	@Override public void run() {

		while (true) {

			if (!queue.isEmpty()) {
				String line;

				line = queue.remove(0);

				if (line == null)
					break;

				parser.process(line);


			}
		}

		if (bonded_analyzer != null)
			bonded_analyzer.finished();

	}

	@Override public boolean part(CharBuffer part) {
		if (!started & (started = true))
			start();
		queue.add(part.toString());

		return true;
	}

	@Override public void finished() {
		queue.add(null);
	}
}
