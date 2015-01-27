package simpleui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
	 * @param a
	 * @param message
	 * @param hour
	 * @param minutes
	 */
	public static void createAlarm(Context a, String message, int hour,
			int minutes) {
		Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
				.putExtra(AlarmClock.EXTRA_MESSAGE, message)
				.putExtra(AlarmClock.EXTRA_HOUR, hour)
				.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
		if (intent.resolveActivity(a.getPackageManager()) != null) {
			a.startActivity(intent);
		}
	}

	/**
	 * needs this permission: com.android.alarm.permission.SET_ALARM
	 * <uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
	 * 
	 * @param message
	 * @param seconds
	 */
	public static void startTimer(Context a, String message, int seconds) {
		Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
				.putExtra(AlarmClock.EXTRA_MESSAGE, message)
				.putExtra(AlarmClock.EXTRA_LENGTH, seconds)
				.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
		if (intent.resolveActivity(a.getPackageManager()) != null) {
			a.startActivity(intent);
		}
	}

	@SuppressLint("NewApi")
	public static void addCalenderEvent(Context a, String title,
			String location, Calendar begin, Calendar end) {
		Intent intent = new Intent(Intent.ACTION_INSERT)
				.setData(Events.CONTENT_URI).putExtra(Events.TITLE, title)
				.putExtra(Events.EVENT_LOCATION, location)
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
				.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
		if (intent.resolveActivity(a.getPackageManager()) != null) {
			a.startActivity(intent);
		}
	}

	/**
	 * @param a
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
	public static void showMapApp(Context a, Uri geoLocation) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(geoLocation);
		if (intent.resolveActivity(a.getPackageManager()) != null) {
			a.startActivity(intent);
		}
	}

	public static boolean openFile(Context a, Uri pathToFile) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(pathToFile);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			a.startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
		}
		return false;
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

	public static void installApp(Context a, String strPackageName) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://details?id=" + strPackageName));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		a.startActivity(intent);
	}

	public static void uninstallApp(Context a, String strPackageName) {
		Uri uri = Uri.fromParts("package", strPackageName, null);
		Intent it = new Intent(Intent.ACTION_DELETE, uri);
		a.startActivity(it);
	}

	public static void showAppDetails(Context a, String strPackageName) {
		Uri uri = Uri.parse("market://details?id=" + strPackageName);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		a.startActivity(it);
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

	public static void launchFacebook(Context a, Intent targetIntent) {
		defineAppToLaunch(a, targetIntent, "facebook");
		a.startActivity(Intent.createChooser(targetIntent, ""));
	}

	public static void launchGooglePlus(Context a, Intent targetIntent) {
		defineAppToLaunch(a, targetIntent, "google", "plus");
		a.startActivity(Intent.createChooser(targetIntent, ""));
	}

	public static void launchGoogleMail(Context a, Intent targetIntent) {
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

	/**
	 * @param context
	 * @param myMailSubject
	 * @param mailText
	 * @param emailAddresses
	 * @param filesToSend
	 *            you can only send files which are located in the storage
	 *            (internal or external SD card e.g.)
	 */
	public static void sendMailIntent(Context context, String myMailSubject,
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
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

}
