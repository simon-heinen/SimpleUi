package simpleui.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public abstract class AlarmEvent extends BroadcastReceiver {

	private static final String LOG_TAG = "AlarmEvent";

	public AlarmEvent() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		onAlarmTriggered(context);
		wl.release();
		Log.i(LOG_TAG, "AlarmEvent.onReceive done");
	}

	/**
	 * this will be called every {@link TmfConstantsFrontend#defaultWakeUpSpeed}
	 * 
	 * @param context
	 */
	public abstract void onAlarmTriggered(Context context);

	public static void registerAlarmEventViaIntent(Context context,
			Class<? extends BroadcastReceiver> receiverClassToStart,
			long nextWakeupTimeInMs, boolean repeatingAlarm) {
		Log.d(LOG_TAG, "registerAlarmEventViaIntent");
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, receiverClassToStart);
		PendingIntent intent = PendingIntent.getBroadcast(context, 0, i, 0);
		alarmManager.cancel(intent);
		long nextWakeupTimestamp = System.currentTimeMillis()
				+ nextWakeupTimeInMs;
		if (repeatingAlarm) {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					nextWakeupTimestamp, nextWakeupTimeInMs, intent);
		} else {
			alarmManager.set(AlarmManager.RTC_WAKEUP, nextWakeupTimestamp,
					intent);
		}
	}

	public static void cancelAlarmViaIntent(Context context,
			Class<? extends BroadcastReceiver> listenerToCancel) {
		Intent intent = new Intent(context, listenerToCancel);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}
