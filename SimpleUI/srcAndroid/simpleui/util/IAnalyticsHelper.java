package simpleui.util;

import android.app.Activity;
import android.content.Context;

public interface IAnalyticsHelper {

	public static final String TRACK_DEFAULT_CATEGORY = "defEvents";

	public void trackStart(Activity a, String screenName);

	public void trackStop(Activity a);

	/**
	 * see {@link IAnalyticsHelper#track(Context, String, String, String, Long)}
	 * 
	 * @param c
	 * @param action
	 * @param label
	 */
	public void track(Context c, String action, String label);

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
	public void track(Context c, String category, String action, String label,
			Long value);
}
