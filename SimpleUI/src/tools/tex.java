package tools;

import android.content.Context;

public class tex {

	private static Context c;

	public static void setContext(Context c) {
		tex.c = c;
	}

	public static String t(int resId) {
		return "" + c.getText(resId);
	}

	public static String t(int resId, String... s) {
		return "" + c.getString(resId, s);
	}
}
