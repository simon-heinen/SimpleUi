package tools;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class IntentHelper {

	public static Intent newSendIntent(File takenPhotoPath) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, IO.toUri(takenPhotoPath));
		share.putExtra(Intent.EXTRA_TITLE, "abc EXTRA_TITLE");
		share.putExtra(Intent.EXTRA_TEXT, "abc EXTRA_TEXT");
		share.putExtra(Intent.EXTRA_SUBJECT, "abc EXTRA_SUBJECT");
		return share;
	}

	public static boolean defineAppToLaunch(Activity a, Intent targetIntent,
			String... keywords) {
		PackageManager pm = a.getApplicationContext().getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(targetIntent,
				0);
		for (final ResolveInfo app : activityList) {
			if (containsAll(keywords, app)) {
				final ActivityInfo activity = app.activityInfo;
				final ComponentName name = new ComponentName(
						activity.applicationInfo.packageName, activity.name);
				targetIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				targetIntent.setComponent(name);
				return true;
			}
		}
		return false;
	}

	private static boolean containsAll(String[] keywords, final ResolveInfo app) {
		for (String keyword : keywords) {
			if (!app.activityInfo.name.contains(keyword)) {
				return false;
			}
		}
		return true;
	}

}
