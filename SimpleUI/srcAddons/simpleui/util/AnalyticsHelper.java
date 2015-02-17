package simpleui.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * Google Analytics tracking: The app specific id has to be placed in an
 * res/values/analytics.xml file (read
 * https://developers.google.com/analytics/devguides/collection/android/v3/ for
 * more information)
 */
public class AnalyticsHelper implements IAnalyticsHelper {
	private static final String LOG_TAG = AnalyticsHelper.class.getSimpleName();

	public AnalyticsHelper() {
		Log.i(LOG_TAG, LOG_TAG + " created");
	}

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

	@Override
	public void trackStop(Activity a) {
		try {
			EasyTracker.getInstance(a).activityStop(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void track(Context c, String action, String label) {
		track(c, TRACK_DEFAULT_CATEGORY, action, label, null);
	}

	@Override
	public void track(Context c, String category, String action, String label,
			Long value) {
		try {
			if (c != null) {
				EasyTracker.getInstance(c).send(
						MapBuilder.createEvent(category, action, label, value)
								.build());
			} else {
				Log.w(LOG_TAG, "track: context was null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
