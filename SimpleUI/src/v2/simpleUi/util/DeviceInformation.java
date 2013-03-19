package v2.simpleUi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Looper;

public class DeviceInformation {
	public static boolean isConnectedToMobileInternet(Context c) {
		// mobile
		State mobile = ((ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		return mobile == NetworkInfo.State.CONNECTED
				|| mobile == NetworkInfo.State.CONNECTING;
	}

	public static boolean isInternetAvailable(Context c) {
		ConnectivityManager connectivityManager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public static boolean isConnectedToWifi(Context c) {
		// wifi
		State wifi = ((ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return wifi == NetworkInfo.State.CONNECTED
				|| wifi == NetworkInfo.State.CONNECTING;
	}

	/**
	 * @return true if the current thread is the UI thread
	 */
	public static boolean isUiThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}
}
