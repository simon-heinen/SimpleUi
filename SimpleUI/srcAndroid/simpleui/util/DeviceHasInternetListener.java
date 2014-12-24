package simpleui.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * you need an manifest entry for this receiver with the following filter:
 * 
 * <receiver android:name="packageXY.YourReceiverClass"> <intent-filter> <action
 * android:name="android.net.conn.CONNECTIVITY_CHANGE" /> </intent-filter>
 * </receiver>
 * 
 * and the permission
 * 
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * 
 */
public abstract class DeviceHasInternetListener extends BroadcastReceiver {

	private static final String LOG_TAG = "NetworkStateReceiver";
	private static final long MIN_WAIT_TIME_IN_MS = 10 * 1000;
	private static long lastTimeOnline = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "Network connectivity change");

		if (intent.getExtras() != null) {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

			long now = System.currentTimeMillis();

			if (ni != null && ni.isConnected()
					&& now - lastTimeOnline > MIN_WAIT_TIME_IN_MS) {
				lastTimeOnline = now;
				onInternetAvailable(context,
						ni.getType() == ConnectivityManager.TYPE_WIFI);
			} else if (intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
				onNoInternetAvailable(context);
			}
		}
	}

	/**
	 * @param context
	 * @param connectedViaWifi
	 *            if false the device has internet but only via the normal gsm
	 *            network etc
	 */
	public abstract void onInternetAvailable(Context context,
			boolean connectedViaWifi);

	public abstract void onNoInternetAvailable(Context context);

}
