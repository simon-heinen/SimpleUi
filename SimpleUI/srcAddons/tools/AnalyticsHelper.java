package tools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class AnalyticsHelper {
	private static final String LOG_TAG = "AnalyticsHelper";

	public static final String TRACK_DEFAULT_CATEGORY = "defEvents";

	public static void trackStart(Activity a, String screenName) {
		try {
			EasyTracker t = EasyTracker.getInstance(a);
			if (screenName != null) {
				t.set(Fields.SCREEN_NAME, screenName);
				MapBuilder map = MapBuilder.createAppView();
				map.set(Fields.SCREEN_NAME, screenName);
				t.send(map.build());
				Log.d(LOG_TAG, "Analytics info sent: 'Showing " + screenName
						+ "'");
			}
			t.activityStart(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void trackStop(Activity a) {
		EasyTracker.getInstance(a).activityStop(a);
	}

	/**
	 * see {@link AnalyticsHelper#track(Context, String, String, String, Long)}
	 * 
	 * @param c
	 * @param action
	 * @param label
	 */
	public static void track(Context c, String action, String label) {
		track(c, TRACK_DEFAULT_CATEGORY, action, label, null);
	}

	/**
	 * https://developers.google.com/analytics/devguides/collection/android/v3/
	 * events
	 * 
	 * @param c
	 * @param category
	 *            the category of the event (you can use the placeholder
	 *            {@link AnalyticsHelper#TRACK_DEFAULT_CATEGORY} or define your
	 *            own one
	 * @param action
	 *            the action like "coinsCollectedIngame"
	 * @param label
	 *            the label like "User collected coins"
	 * @param value
	 *            e.g. the amount of coins the user collected
	 */
	public static void track(Context c, String category, String action,
			String label, Long value) {
		if (c != null) {
			EasyTracker.getInstance(c).send(
					MapBuilder.createEvent(category, action, label, value)
							.build());
		} else {
			Log.w(LOG_TAG, "track: c was null");
		}
	}
}
