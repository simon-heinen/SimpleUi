package simpleui.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Automatically outputs the position in the code where the log information was
 * produced
 * 
 * @author Simon Heinen
 * 
 */
public class Log {

	private static final String SPACER = ">  ";

	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARNING = 4;
	public static final int ERROR = 5;
	public static final int NO_LOGGING = 6;
	private static int currentLoggingLevel = 0;

	/**
	 * sysout logging only if not android device (e.g. server)
	 */
	private static final boolean IS_DESKTOP = SystemUtil.isDesktop();
	private static final boolean IS_ANDROID = SystemUtil.isAndroid();

	private static final String LOG_TAG = "CustomLogger";

	private static ArrayList<String> logHistory = new ArrayList<String>();
	private static Map<Thread, String> threadNames = new HashMap<Thread, String>();

	private static boolean androidLogAvailable = true;

	public static int usedDesktopTabLength = 15;

	private static Method android_Log_v;
	private static Method android_Log_d;

	/**
	 * use {@link Log#setLoggingLevel(int)} instead
	 */
	@Deprecated
	public static void setDEBUG_ENABLED(boolean enableDebugging) {
		if (enableDebugging) {
			currentLoggingLevel = VERBOSE;
		} else {
			currentLoggingLevel = NO_LOGGING;
		}
	}

	public static void setLoggingLevel(int level) {
		currentLoggingLevel = level;
	}

	public static void w(String LOG_TAG, String warning) {
		if (currentLoggingLevel <= WARNING) {
			LOG_TAG += getCurrentThreadName();
			if (IS_DESKTOP) {
				System.err.println(calcDesktopSpacing(LOG_TAG) + " W > "
						+ warning);
			} else {
				logHistory.add(newLogEntry("w", LOG_TAG, warning));
				Logger.getLogger(LOG_TAG).warning(warning);
			}
		}
	}

	public static String getCurrentThreadName() {
		Thread currentThread = Thread.currentThread();
		String name = threadNames.get(currentThread);
		if (name == null) {
			name = currentThread.getName();
			if (name.contains("%")) {
				name = "T." + threadNames.size();
			}
			threadNames.put(currentThread, name);
		}
		return " (" + name + ")";
	}

	public static ArrayList<String> getLogHistory() {
		return logHistory;
	}

	public static void e(String LOG_TAG, String error) {
		if (currentLoggingLevel <= ERROR) {
			LOG_TAG += getCurrentThreadName();
			if (IS_DESKTOP) {
				System.err.println(LOG_TAG + SPACER + error);
			} else {
				logHistory.add(newLogEntry("e", LOG_TAG, error));
				Logger.getLogger(LOG_TAG).severe(error);
			}

		}
	}

	public static void e(String LOG_TAG, Exception e, String errorInfoText) {
		if (currentLoggingLevel <= ERROR) {
			String errorStack = getFirstElementsOfStackTrace(e, 20, "\n");
			if (IS_DESKTOP) {
				System.err.println(LOG_TAG + SPACER + errorInfoText + ": "
						+ errorStack);
				e.printStackTrace();
			} else {
				logHistory.add(newLogEntry("e", LOG_TAG, errorInfoText + ": "
						+ errorStack));
				Logger.getLogger(LOG_TAG).severe(errorInfoText);
				Logger.getLogger(LOG_TAG).severe(errorStack);
				Logger.getLogger(LOG_TAG).severe("" + e);
			}

		}
	}

	public static void i(String LOG_TAG, String info) {
		if (currentLoggingLevel <= INFO) {
			LOG_TAG += getCurrentThreadName();
			if (IS_DESKTOP) {
				System.out
						.println(calcDesktopSpacing(LOG_TAG) + " I > " + info);
			} else {
				logHistory.add(newLogEntry("i", LOG_TAG, info));
				Logger.getLogger(LOG_TAG).info(info);
			}

		}
	}

	public static void d(String LOG_TAG, String debugText) {
		if (currentLoggingLevel <= DEBUG) {
			LOG_TAG += getCurrentThreadName();
			if (IS_DESKTOP) {
				System.out.println(calcDesktopSpacing(LOG_TAG) + " D > "
						+ debugText);
			} else if (IS_ANDROID && androidLogAvailable) {
				// use reflection for debug and verbose levels
				try {
					if (android_Log_d == null) {
						Class logClass = Class.forName("android.util.Log");
						android_Log_d = logClass.getMethod("d", String.class,
								String.class);
					}
					android_Log_d.invoke(null, LOG_TAG, debugText);
				} catch (Exception e) {
					androidLogAvailable = false;
				}
			} else {
				Logger.getLogger(LOG_TAG).config(debugText);
			}
		}
	}

	private static String calcDesktopSpacing(String tag) {
		do {
			tag += " ";
			if (tag.length() > usedDesktopTabLength) {
				usedDesktopTabLength = tag.length();
			}
		} while (tag.length() < usedDesktopTabLength);
		return tag;
	}

	public static void v(String LOG_TAG, String debugText) {
		if (currentLoggingLevel <= VERBOSE) {
			LOG_TAG += getCurrentThreadName();
			if (IS_DESKTOP) {
				System.out.println(calcDesktopSpacing(LOG_TAG) + " V > "
						+ debugText);
			} else if (IS_ANDROID && androidLogAvailable) {
				// use reflection for debug and verbose levels
				try {
					if (android_Log_v == null) {
						Class logClass = Class.forName("android.util.Log");
						android_Log_v = logClass.getMethod("v", String.class,
								String.class);
					}
					android_Log_v.invoke(null, LOG_TAG, debugText);
				} catch (Exception e) {
					androidLogAvailable = false;
				}
			} else {
				Logger.getLogger(LOG_TAG).fine(debugText);
			}
		}
	}

	public static void e(String LOG_TAG, Exception e) {
		e(LOG_TAG, e, getFirstElementsOfStackTrace(e, 4, " "));
	}

	public static String getFirstElementsOfStackTrace(Throwable t,
			int nrOfElements, String separator) {
		if (t == null) {
			return "";
		}
		StackTraceElement[] stackTrace = t.getStackTrace();
		return getElementsOfStackTrace(stackTrace, 0, nrOfElements, separator);
	}

	public static String getLogTag() {
		String logTag = getLineInCodeWhereLogHappened(4);
		if (SystemUtil.isAndroid()) {
			logTag = LOG_TAG;
		}
		return logTag;
	}

	private static String getLineInCodeWhereLogHappened(int depth) {
		return getElementsOfStackTrace(Thread.currentThread().getStackTrace(),
				depth, 1, "");
	}

	public static String getElementsOfStackTrace(
			StackTraceElement[] stackTrace, int start, int nrOfElements,
			String separator) {
		String r = separator;
		for (int i = start; i < nrOfElements + start && i < stackTrace.length; i++) {
			r += stackTrace[i] + separator;
		}
		return r;
	}

	public static void w(String warningText) {
		w(getLogTag(), warningText);
	}

	public static void e(String errorText) {
		e(getLogTag(), errorText);
	}

	public static void e(Exception e, String errorInfoText) {
		e(getLogTag(), e, errorInfoText);
	}

	public static void i(String info) {
		i(getLogTag(), info);
	}

	/**
	 * Call this if you have a lot of debug text to display, else for single
	 * infos user {@link Log#i(String)}
	 * 
	 * @param string
	 */
	public static void d(String string) {
		d(getLogTag(), string);
	}

	public static String generateLogHistoryAsJsonArray() {
		String result = "[";
		for (String s : Log.getLogHistory()) {
			result += s + ",\n";
		}
		return result + "]";
	}

	private static String newLogEntry(String lvl, String LOG_TAG, String warning) {
		return "(" + lvl + ")" + LOG_TAG + " _:_ " + warning;
	}

	private static String lastName;
	private static Long lastStamp;
	private static Map<String, List<Long>> statistics = new HashMap();

	public static Map<String, List<Long>> getStatistics() {
		return statistics;
	}

	/**
	 * m for measure (or monitor), will print out a time how long it took since
	 * the last measure event, useful for loops etc.
	 * 
	 * @param LOG_TAG
	 * @param eventName
	 *            a name which should always be the same
	 * @param eventDescr
	 */
	public static void m(String LOG_TAG, String eventName, String eventDescr) {
		Long stamp = System.currentTimeMillis();

		if (lastName != null) {

			long dt = stamp - lastStamp;

			String t = dt + "ms";
			if (dt > 1000) {
				t = ((dt / 1000)) + "s";
			}

			Log.i(LOG_TAG + " (" + lastName + "->" + t + "->" + eventName + ")",
					eventDescr);

			String key = lastName + " -> " + eventName;
			List<Long> l = statistics.get(key);
			if (l == null) {
				l = new ArrayList();
				statistics.put(key, l);
			}
			l.add(dt);
		}

		lastName = eventName;
		lastStamp = stamp;

	}

	public static void e(String LOG_TAG, String errorMessage, Exception e) {
		e(LOG_TAG, e, errorMessage);
	}
}
