package simpleui.util;

import android.content.Context;

public class tex {

	private static Context c;

	public static void setContext(Context c) {
		tex.c = c;
	}

	public static String t(int resId) {
		if (c == null) {
			c = SimpleUiApplication.getContext();
		}
		return "" + c.getText(resId);
	}

	/**
	 * @param resId
	 * @param s
	 *            use %1$s %2$s ... in the string to replace the passed dynamic
	 *            values. For example a string coud be
	 *            "Eat %1$s apples and %2$s bananas"
	 * @return
	 */
	public static String t(int resId, String... s) {
		if (c == null) {
			c = SimpleUiApplication.getContext();
		}
		return "" + c.getString(resId, s);
	}
}
