package tools;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class IntentHelper {

	public static Intent newSendIntent(File takenPhotoPath, String subjectText,
			String titleText, String descriptionText) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, IO.toUri(takenPhotoPath));
		share.putExtra(Intent.EXTRA_SUBJECT, subjectText);
		share.putExtra(Intent.EXTRA_TITLE, titleText);
		share.putExtra(Intent.EXTRA_TEXT, descriptionText);
		return share;
	}

	public static void testname(Activity a, Intent targetIntent)
			throws Exception {
	}

	public static void launchFacebook(Activity a, Intent targetIntent) {
		defineAppToLaunch(a, targetIntent, "facebook");
		a.startActivity(Intent.createChooser(targetIntent, ""));
	}

	public static void launchGooglePlus(Activity a, Intent targetIntent) {
		defineAppToLaunch(a, targetIntent, "google", "plus");
		a.startActivity(Intent.createChooser(targetIntent, ""));
	}

	public static void launchGoogleMail(Activity a, Intent targetIntent) {
		defineAppToLaunch(a, targetIntent, "google", "gm");
		a.startActivity(Intent.createChooser(targetIntent, ""));
	}

	public static boolean defineAppToLaunch(Context c, Intent targetIntent,
			String... keywords) {
		PackageManager pm = c.getApplicationContext().getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(targetIntent,
				0);
		for (final ResolveInfo app : activityList) {
			if (containsAll(keywords, app)) {
				final ActivityInfo activity = app.activityInfo;
				final ComponentName name = new ComponentName(
						activity.applicationInfo.packageName, activity.name);
				targetIntent.setComponent(name);
				return true;
			}
		}
		return false;
	}

	private static boolean containsAll(String[] keywords, final ResolveInfo app) {
		for (String keyword : keywords) {
			if (!app.activityInfo.packageName.contains(keyword)) {
				return false;
			}
		}
		return true;
	}

}
