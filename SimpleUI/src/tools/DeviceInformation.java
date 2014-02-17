package tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;

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

	public static boolean isPositioningViaWifiEnabled(Context context) {
		ContentResolver cr = context.getContentResolver();
		String enabledProviders = Settings.Secure.getString(cr,
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!TextUtils.isEmpty(enabledProviders)) {
			// not the fastest way to do that :)
			String[] providersList = TextUtils.split(enabledProviders, ",");
			for (String provider : providersList) {
				if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isScreenOn(Context context) {
		return ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
				.isScreenOn();
	}

	public static boolean isConnectedToWifi(Context c) {
		// via vwifi
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

	/**
	 * @param a
	 * @return the size with size.x=width and size.y=height
	 */
	@SuppressLint("NewApi")
	public static Point getScreenSize(Activity a) {
		Point size = new Point();
		Display d = a.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			d.getSize(size);
		} else {
			size.x = d.getWidth();
			size.y = d.getHeight();
		}
		return size;
	}
}
