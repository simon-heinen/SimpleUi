package simpleui.util;

import android.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Started before options are shown, stopped on return. Will prevent Android to
 * kill the process
 * 
 * dont forget to register it in your manifest like this:
 * 
 * < service android:name="v2.simpleUi.util.KeepProcessAliveService" >
 * </service>
 * 
 * @author Simon Heinen
 * 
 */
public class KeepProcessAliveService extends IntentService {

	private final static int NOTIFICATION_ID = 5261;
	private static final String LOG_TAG = "KeepProcessAliveService";
	private static String tickerText = "";
	private static int KEEP_ALIVE_TIME;

	public KeepProcessAliveService() {
		super("KeepProcessAliveService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int wakeUpFrquency = 2;
		if (KEEP_ALIVE_TIME > 0) {
			showNotification(this);
		}
		Log.d(LOG_TAG, "Started KeepProcessAliveService");
		for (int i = 0; i < KEEP_ALIVE_TIME * wakeUpFrquency; i++) {
			try {
				Thread.sleep(1000 / wakeUpFrquency);
				// System.out.println("i=" + i);
			} catch (InterruptedException e) {
			}
		}
		((NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE))
				.cancel(NOTIFICATION_ID);
	}

	public static void stopKeepAliveService() {
		KEEP_ALIVE_TIME = 0;
		Log.i(LOG_TAG, "Stoping KeepProcessAliveService");
	}

	public static void startKeepAliveService(Context c) {
		KEEP_ALIVE_TIME = 100000;
		Log.i(LOG_TAG, "Trying to start KeepProcessAliveService");
		c.startService(new Intent(c, KeepProcessAliveService.class));
	}

	public static void showNotification(final Context c) {

		int icon = R.drawable.ic_dialog_info;
		long when = System.currentTimeMillis();
		final Notification notification = new Notification(icon, tickerText,
				when);

		PendingIntent contentIntent = PendingIntent.getActivity(c, 0,
				new Intent(), 0);
		String contentTitle = tickerText; // TODO?
		String contentText = "";
		notification.setLatestEventInfo(c, contentTitle, contentText,
				contentIntent);
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE
				| Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_ONGOING_EVENT;
		((NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE))
				.notify(NOTIFICATION_ID, notification);

	}

}
