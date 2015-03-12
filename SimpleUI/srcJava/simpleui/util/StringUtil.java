package simpleui.util;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * If high performance is needed, instead of using {@link Pattern} (which is
	 * very slow on Android) this method can be used for splitting stings based
	 * on single chars
	 * 
	 * @param s
	 *            the String as a char array (use {@link String#toCharArray()})
	 * @param splitChar
	 *            the character to slit the string by
	 * @return the resulting list of words (can be converted to an
	 *         {@link String} array using {@link ArrayList#toArray(Object[])}
	 */
	public static ArrayList<String> splitBySingleChar(final char[] s,
			final char splitChar) {
		final ArrayList<String> result = new ArrayList<String>();
		final int length = s.length;
		int offset = 0;
		int count = 0;
		for (int i = 0; i < length; i++) {
			if (s[i] == splitChar) {
				if (count > 0) {
					result.add(new String(s, offset, count));
				}
				offset = i + 1;
				count = 0;
			} else {
				count++;
			}
		}
		if (count > 0) {
			result.add(new String(s, offset, count));
		}
		return result;
	}

}
