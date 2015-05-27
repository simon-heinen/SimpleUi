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

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "Network connectivity changed");
		if (intent.getExtras() != null) {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
			if (ni != null && ni.isConnected()) {
				onInternetAvailable(context,
						ni.getType() == ConnectivityManager.TYPE_WIFI, ni);
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
			boolean connectedViaWifi, NetworkInfo ni);

	public abstract void onNoInternetAvailable(Context context);

}
