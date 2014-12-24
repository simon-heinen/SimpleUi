package simpleui.util;

import android.app.Activity;
import android.content.Context;

public class AnalyticsHelperNoOp implements IAnalyticsHelper {

	@Override
	public void trackStart(Activity a, String screenName) {
	}

	@Override
	public void trackStop(Activity a) {
	}

	@Override
	public void track(Context c, String action, String label) {
	}

	@Override
	public void track(Context c, String category, String action, String label,
			Long value) {
	}

}
