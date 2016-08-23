package com.cab404.moonlight.parser;

import com.cab404.moonlight.parser.Tag.Type;
import com.cab404.moonlight.util.SU;

/**
 * Just a simple tag parser.
 *
 * @author cab404
 */
public class TagParser {
	private StringBuilder buffer;
	private TagHandler handler;
	private StringBuilder full_data;

	/**
	 * It's StringBuilder, but still, do not change it's contents manually.
	 * Please.
	 * It can not only break all tags, produced by this parser, but if this
	 * library happens to be it production, your colleagues may break some
	 * of your bones too.
	 */
	public CharSequence getHTML() {
		return full_data;
	}

	/**
	 * Till this class doesn't actually needs any of tags it parses,
	 * it will throw all of them to thing you'll stick in here.
	 */
	public void setTagHandler(TagHandler handler) {
		this.handler = handler;
	}

	public TagParser() {
		full_data = new StringBuilder();
		buffer = new StringBuilder();
	}

	private static final String
			COMM_START = "<!--",
			COMM_END = "-->",
			TAG_START = "<",
			TAG_END = ">";

	private int prev = 0;
	private int j = 0;

	/**
	 * Takes a chunk of text, appends it to buffer and full HTML, then tries to find some new tags.
	 * <p/>
	 * <br/>
	 * <br/><strong>Q</strong>: Why not TagSoup?
	 * <br/><strong>A</strong>: IDK.
	 * <br/>
	 * <br/>
	 * <br/><strong>Q</strong>: Why haven't you created TagInputStream?
	 * <br/><strong>A</strong>: IDK.
	 * <br/>
	 * <br/><strong>Q</strong>: Why are you using buffer AND full_data?
	 * <br/><strong>A</strong>: I thought it would be faster and safer (and more fun for me)
	 * to work with small buffer instead of full page. I might be terribly wrong.
	 * <br/>
	 * <br/><strong>Q</strong>: Can I feed it with raw bytes?
	 * <br/><strong>A</strong>: I never tried, but I suppose it will become sentient afterwards.
	 */
	public synchronized void process(String chunk) {
		buffer.append(chunk);
		full_data.append(chunk);

		int comment_start;

		while ((comment_start = buffer.indexOf(COMM_START)) != -1) {

			int comment_end = buffer.indexOf(COMM_END, comment_start);

			if (comment_end == -1)
				return;

			comment_end += COMM_END.length();

			//// Oh well, buck everything. It's midnight, baby. Deleting em' all and everythere!
			// Don't ask me, why whitespaces. Just. Don't. It is a representation of comment space in full data,
			// so comment will be treated as whitespaces in parsing, and will be left in it's true form in text.
			for (int i = comment_start; i < comment_end; i++)
				buffer.setCharAt(i, ' ');

//            full_data.delete(prev + comment_start, prev + comment_end);
//            buffer.delete(comment_start, comment_end);
		}


		while (true) {
			// Limiting everything to start of comment, if any. Comments are painful.
			int e_index = buffer.indexOf(COMM_START);
			int i;

			i = buffer.indexOf(TAG_START, 0);
			j = buffer.indexOf(TAG_END, i);

			if (i == -1 || (e_index != -1 && i > e_index)) break;

			int breaker = buffer.indexOf(TAG_START, i + 1);

			if (j == -1 || (e_index != -1 && j >= e_index)) break;

			// detecting broken tags inclusion problems
			if (breaker != -1 && breaker < j) {
				j = breaker - 1;
				step();
				continue;
			}

			Tag tag = new Tag();
			tag.type = Type.OPENING;
			tag.start = prev + i;
			tag.end = prev + j + 1;
			tag.text = buffer.substring(i, j + 1);

			String inner = buffer.substring(i + 1, j);
			int l = inner.length() - 1;

			if (l == -1) {
				step();
				continue;
			}

			if (inner.charAt(0) == '/') {
				tag.type = Type.CLOSING;
				inner = buffer.substring(i + 2, j);
			} else if (inner.charAt(l) == '/') {
				tag.type = Type.STANDALONE;
				inner = buffer.substring(i + 1, j - 1);
			}


//			List<String> name_and_everything_else = SU.charSplit(inner.trim(), 2, ' ');
			String[] name_and_everything_else = SU.splitToArray(inner.trim(), 2, ' ');

			tag.name = name_and_everything_else[0].trim();

			if (tag.name.isEmpty() || tag.name.charAt(0) == '!') // Handling !doctype
				tag.type = Type.COMMENT;

			if (name_and_everything_else.length == 2) {
				// Parsing properties.
				String params = name_and_everything_else[1].trim();
				String key = null;

				// Current temporary position.
				int s = 0;

				// If we are parsing value in ' (true) or " (false) boundaries
				boolean quot = false;

				// 0 - searching for end of key,
				// 1 - searching for a start of value,
				// 2 - searching the end of value.
				byte mode = 0;

				for (int index = 0; index < params.length(); index++) {
					char current = params.charAt(index);

					if (mode == 0 && current == '=') {
						key = params.substring(s, index);
						mode = 1;
						continue;
					}

					if (mode == 1 && (current == '"' || current == '\'')) {
						quot = current == '\'';
						s = index + 1;
						mode = 2;
						continue;
					}

					if (mode == 2 && current == (quot ? '\'' : '"')) {
						tag.props.put(key.trim(), params.substring(s, index));
						s = index + 1;
						mode = 0;
					}

				}

			}

			step();
			handler.handle(tag);

		}


	}

	/**
	 * Shrinks input buffer and moves counters to new positions.
	 */
	private void step() {
		buffer.delete(0, j + 1);
		prev += j + 1;
	}

	public interface TagHandler {
		void handle(Tag tag);
	}

}
