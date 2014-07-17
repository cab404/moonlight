package com.cab404.moonlight.parser;

import com.cab404.moonlight.framework.BlockProvider;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * HTML tree analyzing thread.
 *
 * @author cab404
 * @see com.cab404.moonlight.parser.HTMLTagParserThread
 */
public class HTMLAnalyzerThread extends Thread implements TagParser.TagHandler, BlockProvider {
	private CopyOnWriteArrayList<Tag> queue;
	private LevelAnalyzer analyzer;
	private boolean started = false;


	public HTMLAnalyzerThread(CharSequence data) {
		queue = new CopyOnWriteArrayList<>();
		this.analyzer = new LevelAnalyzer(data);
		setDaemon(true);
	}

	public void setBlockHandler(LevelAnalyzer.BlockHandler handler) {
		analyzer.setBlockHandler(handler);
	}

	@Override public void run() {

		while (true) {

			if (!queue.isEmpty()) {
				Tag tag = queue.remove(0);
				if (tag == null)
					break;
				analyzer.add(tag);
			}

		}

	}

	@Override public void handle(Tag tag) {
		if (!started & (started = true))
			start();

		queue.add(tag);
	}

	public void finished() {
		queue.add(null);
	}

}
