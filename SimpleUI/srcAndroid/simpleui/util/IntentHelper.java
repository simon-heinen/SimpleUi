package simpleui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;

public class IntentHelper {

	private static final String LOG_TAG = IntentHelper.class.getSimpleName();

	public static Intent newSendImageIntent(File takenPhotoPath,
			String subjectText, String titleText, String descriptionText) {
		return newImageIntent(takenPhotoPath, subjectText, titleText,
				descriptionText, Intent.ACTION_SEND);
	}

	public static Intent newEditImageIntent(File takenPhotoPath,
			String subjectText, String titleText, String descriptionText) {
		return newImageIntent(takenPhotoPath, subjectText, titleText,
				descriptionText, Intent.ACTION_EDIT);
	}

	/**
	 * needs this permission: com.android.alarm.permission.SET_ALARM
	 * <uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
	 * 
	 * @param c
	 * @param message
	 * @param hour
	 * @param minutes
	 */
	public static void createAlarm(Context c, String message, int hour,
			int minutes) {
		Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
				.putExtra(AlarmClock.EXTRA_MESSAGE, message)
				.putExtra(AlarmClock.EXTRA_HOUR, hour)
				.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
		if (intent.resolveActivity(c.getPackageManager()) != null) {
			c.startActivity(intent);
		}
	}

	/**
	 * needs this permission: com.android.alarm.permission.SET_ALARM
	 * <uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
	 * 
	 * @param message
	 * @param seconds
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void startTimer(Context c, String message, int seconds) {
		Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
				.putExtra(AlarmClock.EXTRA_MESSAGE, message)
				.putExtra(AlarmClock.EXTRA_LENGTH, seconds)
				.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
		if (intent.resolveActivity(c.getPackageManager()) != null) {
			c.startActivity(intent);
		}
	}

	public static void addCalenderEvent(Context c, String title,
			String location, Calendar begin, Calendar end) {
		Intent intent = new Intent(Intent.ACTION_INSERT)
				.setData(Events.CONTENT_URI).putExtra(Events.TITLE, title)
				.putExtra(Events.EVENT_LOCATION, location)
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
				.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
		if (intent.resolveActivity(c.getPackageManager()) != null) {
			c.startActivity(intent);
		}
	}

	/**
	 * @param c
	 * @param geoLocation
	 * 
	 *            geo:latitude,longitude Show the map at the given longitude and
	 *            latitude. Example: "geo:47.6,-122.3" <br>
	 * <br>
	 *            geo:latitude,longitude?z=zoom Show the map at the given
	 *            longitude and latitude at a certain zoom level. A zoom level
	 *            of 1 shows the whole Earth, centered at the given lat,lng. The
	 *            highest (closest) zoom level is 23. Example:
	 *            "geo:47.6,-122.3?z=11"<br>
	 * <br>
	 *            geo:0,0?q=lat,lng(label) Show the map at the given longitude
	 *            and latitude with a string label. Example:
	 *            "geo:0,0?q=34.99,-106.61(Treasure)"<br>
	 * <br>
	 *            geo:0,0?q=my+street+address Show the location for
	 *            "my street address" (may be a specific address or location
	 *            query). Example: "geo:0,0?q=1600+Amphitheatre+Parkway%2C+CA"<br>
	 * <br>
	 *            Note: All strings passed in the geo URI must be encoded. For
	 *            example, the string 1st & Pike, Seattle should become
	 *            1st%20%26%20Pike%2C%20Seattle. Spaces in the string can be
	 *            encoded with %20 or replaced with the plus sign (+).
	 */
	public static void showMapApp(Context c, Uri geoLocation) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(geoLocation);
		if (intent.resolveActivity(c.getPackageManager()) != null) {
			c.startActivity(intent);
		}
	}

	public static boolean openFile(Context c, Uri pathToFile) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(pathToFile);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			c.startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
		}
		return false;
	}

	public static void openLink(Context c, String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		c.startActivity(i);
	}

	public static boolean openPdfFile(Context a, Uri pathToPdf) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(pathToPdf, "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			a.startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
		}
		return false;
	}

	public static boolean openVideoFile(Context a, Uri pathToVideo) {
		Intent intent = new Intent(Intent.ACTION_VIEW, pathToVideo);
		intent.setDataAndType(pathToVideo, "video/*");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			a.startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
		}
		return false;
	}

	/**
	 * Open another app.
	 * 
	 * @param c
	 *            current Context, like Activity, App, or Service
	 * @param strPackageName
	 *            the full package name of the app to open
	 * @return true if likely successful, false if unsuccessful
	 */
	public static boolean startApp(Context c, String strPackageName) {
		try {
			PackageManager manager = c.getPackageManager();
			Intent i = manager.getLaunchIntentForPackage(strPackageName);
			if (i == null) {
				return false;
			}
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			c.startActivity(i);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void startOrInstallApp(Context c, String strPackageName) {
		if (isAppInstalled(c, strPackageName)) {
			startApp(c, strPackageName);
		} else {
			installApp(c, strPackageName);
		}
	}

	public static boolean isAppInstalled(Context c, String strPackageName) {
		try {
			c.getPackageManager().getPackageInfo(strPackageName,
					PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public static void installApp(Context c, String strPackageName) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + strPackageName));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			c.startActivity(intent);
		} catch (android.content.ActivityNotFoundException e) {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id="
							+ strPackageName));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			c.startActivity(intent);
		}
	}

	public static void updateApp(Context c) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://details?id="
						+ c.getApplicationContext().getPackageName()));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(intent);
	}

	public static void uninstallApp(Context c, String strPackageName) {
		Uri uri = Uri.fromParts("package", strPackageName, null);
		Intent it = new Intent(Intent.ACTION_DELETE, uri);
		c.startActivity(it);
	}

	public static void showAppDetails(Context c, String strPackageName) {
		Uri uri = Uri.parse("market://details?id=" + strPackageName);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		c.startActivity(it);
	}

	private static Intent newImageIntent(File takenPhotoPath,
			String subjectText, String titleText, String descriptionText,
			String actionToDoWithImage) {
		Intent share = new Intent(actionToDoWithImage);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, IO.toUri(takenPhotoPath));
		share.putExtra(Intent.EXTRA_SUBJECT, subjectText);
		share.putExtra(Intent.EXTRA_TITLE, titleText);
		share.putExtra(Intent.EXTRA_TEXT, descriptionText);
		return share;
	}

	public static void launchFacebook(Context c, Intent targetIntent) {
		defineAppToLaunch(c, targetIntent, "facebook");
		c.startActivity(Intent.createChooser(targetIntent, ""));
	}

	public static void launchGooglePlus(Context c, Intent targetIntent) {
		defineAppToLaunch(c, targetIntent, "google", "plus");
		c.startActivity(Intent.createChooser(targetIntent, ""));
	}

	public static void launchGoogleMail(Context c, Intent targetIntent) {
		defineAppToLaunch(c, targetIntent, "google", "gm");
		c.startActivity(Intent.createChooser(targetIntent, ""));
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

	/**
	 * @param c
	 * @param myMailSubject
	 * @param mailText
	 * @param emailAddresses
	 * @param filesToSend
	 *            you can only send files which are located in the storage
	 *            (internal or external SD card e.g.)
	 */
	public static void sendMailIntent(Context c, String myMailSubject,
			String mailText, String[] emailAddresses, List<File> filesToSend) {

		ArrayList<Uri> uris = new ArrayList<Uri>();
		if (filesToSend != null && !filesToSend.isEmpty()) {
			// convert from paths to Android friendly Parcelable Uri's
			for (File fileIn : filesToSend) {
				Uri u = Uri.fromFile(fileIn);
				if (fileIn.exists() && fileIn.canRead()) {
					uris.add(u);
				} else {
					Log.w(LOG_TAG, "Can't attach file: " + fileIn);
					Log.d(LOG_TAG, "file.exists()=" + fileIn.exists());
					Log.d(LOG_TAG, "file.canRead()=" + fileIn.canRead());
				}
			}
		}
		// need to "send multiple" to get more than one attachment
		Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND_MULTIPLE);
		if (uris.size() <= 1) {
			// if no (or one) files appended use the default intent type
			emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		}
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				myMailSubject);
		// emailIntent.setType("plain/text");
		emailIntent.setType("message/rfc822");
		emailIntent
				.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);
		emailIntent.putExtra(Intent.EXTRA_TEXT, mailText);
		if (!uris.isEmpty()) {
			if (uris.size() == 1) {
				// if only one file attach only the one file, otherwise problems
				// in gmail e.g.
				emailIntent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
			} else if (uris.size() > 1) {
				emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
						uris);
			}
		}
		c.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

}
