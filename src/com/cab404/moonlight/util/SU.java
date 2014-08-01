package com.cab404.moonlight.util;

import com.cab404.moonlight.util.exceptions.NotFoundFail;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * String utils.
 *
 * @author cab404
 */
public class SU {
	/**
	 * Splits string without reqexes
	 */
	public static List<String> split(String source, String not_a_regex) {
		ArrayList<String> out = new ArrayList<>();
		int i = 0;
		while (true) {
			int j = source.indexOf(not_a_regex, i);

			if (j == -1) {
				out.add(source.substring(i));
				break;
			}
			out.add(source.substring(i, j));
			i = j + not_a_regex.length();
		}
		return out;
	}
	/**
	 * Splits string without reqexes
	 */
	public static List<String> split(String source, String not_a_regex, int limit) {
		ArrayList<String> out = new ArrayList<>();
		int i = 0;
		while (true) {
			int j = source.indexOf(not_a_regex, i);

			if (j == -1 || out.size() + 1 == limit) {
				out.add(source.substring(i));
				break;
			}
			out.add(source.substring(i, j));
			i = j + not_a_regex.length();
		}
		return out;
	}


	/**
	 * Returns true if check contains symbol ch
	 */
	public static boolean contains(char[] check, char ch) {
		for (int i = 0; i < check.length; i++)
			if (check[i] == ch)
				return true;
		return false;
	}

	public static List<String> charSplit(String source, char ch) {
		return charSplit(source, source.length() + 1, ch);
	}

	/**
	 * Splits string using ch with limit of parts
	 */
	public static List<String> charSplit(String source, int limit, char ch) {
		return new ArrayList<>(Arrays.asList(splitToArray(source, limit, ch)));
	}


	/**
	 * Splits string using any of chars from "chars"
	 * For instance, call<br/>
	 * charSplit("test test, test.test", " .,") <br/>
	 * will return you [test, test, test, test]
	 */
	public static String[] splitToArray(String source, char ch) {
		return splitToArray(source, source.length() + 1, ch);
	}


	/**
	 * Splits string using any of chars from "chars"
	 * For instance, call<br/>
	 * charSplit("test test, test.test", 2, " .,") <br/>
	 * will return you ["test", "test, test.test"]
	 */
	public static String[] splitToArray(String source, int limit, char ch) {
		int occurences = 0;

		occurences += count(source, ch);

		String[] out = new String[(occurences + 1) > limit ? limit : (occurences + 1)];
		int last = 0;
		int array_index = 0;

		for (int i = 0; i < source.length(); i++) {
			if (ch == source.charAt(i)) {
				out[array_index++] = source.substring(last, i);
				last = i + 1;

				if (array_index + 1 == limit)
					break;

			}
		}

		out[array_index] = source.substring(last);

		return out;
	}


	public static int count(CharSequence seq, char ch) {
		int counter = 0;
		for (int i = 0; i < seq.length(); i++)
			if (seq.charAt(i) == ch)
				counter++;
		return counter;
	}


	public static boolean fast_match(String regex, String data) {
		int count = count(regex, '*');

		if (count == 0)
			return data.equals(regex);

		String[] strings = splitToArray(regex, '*');

		if (!(data.startsWith(strings[0])) && data.endsWith(strings[strings.length - 1]))
			return false;

		int s, f = 0;

		for (String str : strings) {
			s = data.indexOf(str, f);
			f = s + str.length();

			if (s == -1)
				return false;
		}

		return true;
	}


	/**
	 * String.substring, but with Strings instead of indexes.
	 */
	public static String sub(String source, String start, String end) {

		int sIndex = source.indexOf(start);
		if (sIndex == -1) {
			throw new NotFoundFail("Error while parsing string " + source + ", no start position found.");
		}
		sIndex += start.length();

		int eIndex = source.indexOf(end, sIndex);
		if (eIndex == -1) {
			throw new NotFoundFail("Error while parsing string " + source + ", no end position found.");
		}
		return source.substring(sIndex, eIndex);
	}
	/**
	 * Backwards sub, just like sub, but will start searching from end of string.
	 */
	public static String bsub(String source, String end, String start) {
		int sIndex = source.lastIndexOf(start);
		if (sIndex == -1) {
			throw new NotFoundFail("Error while parsing string " + source + ", no start position found.");
		}

		int eIndex = source.lastIndexOf(end, sIndex);
		if (eIndex == -1) {
			throw new NotFoundFail("Error while parsing string " + source + ", no end position found.");
		}
		return source.substring(eIndex + end.length(), sIndex);
	}
	/**
	 * URLEncoder.encode()
	 */
	public static String rl(String toConvert) {
		try {
			return URLEncoder.encode(toConvert, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	/**
	 * URLDecoder.decode()
	 */
	public static String drl(String toConvert) {
		try {
			return URLDecoder.decode(toConvert, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * Joins collection of strings using supplied delimeter
	 */
	public static String join(Collection<String> strings, String delimeter) {
		StringBuilder out = new StringBuilder();
		Iterator<String> iterator = strings.iterator();

		while (iterator.hasNext())
			out.append(iterator.next()).append(iterator.hasNext() ? delimeter : "");

		return out.toString();
	}

	private static String[][] html_entities = {
			{"&quot;", "\""},
			{"&rlm;", " ‏"},
			{"&amp;", "&"},
			{"&ndash;", "–"},
			{"&lt;", "<"},
			{"&mdash;", "—"},
			{"&gt;", ">"},
			{"&lsquo;", "‘"},
			{"&OElig;", "Œ"},
			{"&rsquo;", "’"},
			{"&oelig;", "œ"},
			{"&sbquo;", "‚"},
			{"&Scaron;", "Š"},
			{"&ldquo;", "“"},
			{"&scaron;", "š"},
			{"&rdquo;", "”"},
			{"&Yuml;", "Ÿ"},
			{"&bdquo;", "„"},
			{"&circ;", "ˆ"},
			{"&dagger;", "†"},
			{"&tilde;", "˜"},
			{"&Dagger;", "‡"},
			{"&ensp;", " "},
			{"&permil;", "‰"},
			{"&emsp;", " "},
			{"&lsaquo;", "‹"},
			{"&thinsp;", " "},
			{"&rsaquo;", "›"},
			{"&zwnj;", " "},
			{"&euro;", "€"},
			{"&zwj;", " "},
			{"&lrm;", " "},
			{"&#039;", "'"}
	};

	/**
	 * Decodes HTML entities.
	 */
	public static String deEntity(String in) {
		StringBuilder data = new StringBuilder(in);
		for (String[] replace : html_entities) {
			String a = replace[0];
			String b = replace[1];

			int i;
			while ((i = data.indexOf(a)) != -1) {
				data.replace(i, i + a.length(), b);
			}

		}
		return data.toString();
	}

	/**
	 * Searches for HTML tags and removes them. Without regexes.
	 */
	public static String removeAllTags(String toProcess) {
		int s;
		while ((s = toProcess.indexOf('<')) != -1) {
			int f = toProcess.indexOf('>', s);
			if (f == -1) break;
			toProcess = toProcess.substring(0, s) + toProcess.substring(f + 1);
		}
		return toProcess;
	}

	/**
	 * Returns String with supplied amount of tabs.
	 */
	public static String tabs(int num) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < num; i++) tabs.append("\t");
		return tabs.toString();
	}

	/**
	 * Well, it's pretty powerful logging... thing.
	 * It creates tables and supports cell gravity.
	 * <p/>
	 * Every supplied...
	 * ...integer will set column width.
	 * ...Gravity will set the gravity of cell.
	 * ...string will be appended.
	 */
	public static String table(Object... entries) {
		StringBuilder line = new StringBuilder("|");

		int column = 10;
		Gravity ft = Gravity.CENTER;

		for (Object entry : entries) {
			if (entry instanceof Integer) {
				column = (Integer) entry;
			} else if (entry instanceof Gravity) {
				ft = (Gravity) entry;
			} else if (entry instanceof CharSequence) {
				line
						.append(fillSpaces(entry.toString(), column, 0, ft))
						.append("|");
			}
		}
		return line.toString();
	}


	public static enum Gravity {
		RIGHT, LEFT, CENTER
	}

	/**
	 * Fills string with spaces to given length, according to gravity and offset.
	 */
	public static String fillSpaces(String fill, int num, int offset, Gravity gravity) {
		int left = 0, right = 0;
		num -= offset * 2;

		switch (gravity) {
			case RIGHT:
				right = offset;
				left = offset + num - fill.length();
				break;
			case LEFT:
				left = offset;
				right = offset + num - fill.length();
				break;
			case CENTER:
				right = (int) Math.floor((float) (num - fill.length()) / 2) + offset;
				left = (int) Math.ceil((float) (num - fill.length()) / 2) + offset;
				break;
		}

		return spaces(left) + fill + spaces(right);

	}

	/**
	 * Returns given amount of spaces.
	 */
	public static String spaces(int num) {
		StringBuilder spaces = new StringBuilder();
		for (int i = 0; i < num; i++)
			spaces.append(" ");
		return spaces.toString();
	}

	/**
	 * Deletes recurring chars.<br/>
	 * <pre>("  a  b  c", ' ') = " a b c"</pre>
	 */
	public static CharSequence removeRecurringChars(String in, char remove) {
		StringBuilder modify = new StringBuilder(in);

		for (int i = 0; i < modify.length() - 1; ) {
			if (modify.charAt(i) == remove) {
				while ((i + 1 < modify.length() - 1) && modify.charAt(i + 1) == remove) {
					modify.deleteCharAt(i);
				}
			}
			i++;
		}

		return modify;
	}

	/**
	 * (DoNotAskMeWhyI'veDoneThis) = Do not ask me why i've done this
	 */
	public static CharSequence camelCaseToStr(CharSequence camelCase) {
		StringBuilder builder = new StringBuilder(camelCase);

		for (int i = 0; i < builder.length(); i++) {
			if (Character.isUpperCase(builder.charAt(i))) {
				builder.replace(i, i + 1, " " + Character.toLowerCase(builder.charAt(i)));
			}
		}

		if (builder.charAt(0) == ' ') builder.deleteCharAt(0);
		builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));

		return builder;
	}

	/**
	 * Trims spaces in a CharSequence
	 */
	public static CharSequence trim(CharSequence seq) {
		if (seq.length() == 0) return seq;
		int start = 0;
		int end = seq.length() - 1;
		for (; start < seq.length(); start++) if (seq.charAt(start) != ' ') break;
		for (; end >= start; end--) if (seq.charAt(end) != ' ') break;
		return seq.subSequence(start, end);
	}

}
