package simpleui.util;

import simpleui.util.IAnalyticsHelper;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class AnalyticsHelper implements IAnalyticsHelper {
	private static final String LOG_TAG = "AnalyticsHelper";

	public static final String TRACK_DEFAULT_CATEGORY = "defEvents";

	public AnalyticsHelper() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.AAA#trackStart(android.app.Activity, java.lang.String)
	 */
	@Override
	public void trackStart(Activity a, String screenName) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.AAA#trackStop(android.app.Activity)
	 */
	@Override
	public void trackStop(Activity a) {
		EasyTracker.getInstance(a).activityStop(a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.AAA#track(android.content.Context, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void track(Context c, String action, String label) {
		track(c, TRACK_DEFAULT_CATEGORY, action, label, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.AAA#track(android.content.Context, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public void track(Context c, String category, String action, String label,
			Long value) {
		if (c != null) {
			EasyTracker.getInstance(c).send(
					MapBuilder.createEvent(category, action, label, value)
							.build());
		} else {
			Log.w(LOG_TAG, "track: c was null");
		}
	}
}
