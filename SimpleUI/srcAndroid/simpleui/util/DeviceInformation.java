package simpleui.util; 

import java.util.Enumeration;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
/*
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
 */
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;

public class DeviceInformation {
    public static boolean isConnectedToMobileInternet(Context c) throws NullPointerException {
        // mobile
        ConnectivityManager connectivityManager = ((ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        if( connectivityManager == null )
            throw new NullPointerException();
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if( networkInfo == null )
            throw new NullPointerException();
        State mobile = networkInfo.getState();

        return mobile == NetworkInfo.State.CONNECTED
                || mobile == NetworkInfo.State.CONNECTING;
    }

    public static boolean isInternetAvailable(Context c) throws NullPointerException {
        ConnectivityManager connectivityManager = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if( connectivityManager == null )
            throw new NullPointerException();
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * use {@link SystemUtil#isGlass()} instead
     *
     * @return

    @Deprecated
    public static boolean isGlass() {
        return SystemUtil.isGlass();
    }
    */

    public static String getInfosAboutDevice(Activity a) {
        String s = "";
        /*
        try {
            PackageInfo pInfo = a.getPackageManager().getPackageInfo(
                    a.getPackageName(), PackageManager.GET_META_DATA);
            s += "\n APP Package Name: " + a.getPackageName();
            s += "\n APP Version Name: " + pInfo.versionName;
            s += "\n APP Version Code: " + pInfo.versionCode;
            s += "\n";
        } catch (NameNotFoundException e) {
        }
         */
        s += "\n OS Version: " + System.getProperty("os.version") + " ("
                + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " ("
                + android.os.Build.PRODUCT + ")";
        // TODO add application version!

        // more from
        // http://developer.android.com/reference/android/os/Build.html :
        s += "\n Manufacturer: " + android.os.Build.MANUFACTURER;
        s += "\n Other TAGS: " + android.os.Build.TAGS;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        a.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        s += "\n screenWidth: "
                + displaymetrics.widthPixels;
        s += "\n screenHeigth: "
                + displaymetrics.heightPixels;
        s += "\n Keyboard available: "
                + (a.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);

        s += "\n Trackball available: "
                + (a.getResources().getConfiguration().navigation == Configuration.NAVIGATION_TRACKBALL);
        s += "\n SD Card state: " + Environment.getExternalStorageState();
        Properties p = System.getProperties();
        Enumeration keys = p.keys();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            s += "\n > " + key + " = " + (String) p.get(key);
        }
        return s;
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

    public static boolean isScreenOn(Context context) throws NullPointerException {
        PowerManager powerManager = ((PowerManager) context
                .getSystemService(Context.POWER_SERVICE));
        if( powerManager == null )
            throw new NullPointerException();
        return powerManager.isScreenOn();
    }

    public static boolean isConnectedToWifi(Context c) throws NullPointerException {
        // via wifi
        ConnectivityManager connectivityManager = ((ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        if( connectivityManager == null )
            throw new NullPointerException();
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if( networkInfo == null )
            throw new NullPointerException();
        State wifi = networkInfo.getState();
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
    public static Point getScreenSize(Activity a) {
        return getScreenSize(a.getWindowManager().getDefaultDisplay());
    }

    @SuppressLint("NewApi")
    public static Point getScreenSize(Display d) {
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            d.getSize(size);
        } else {
            size.x = d.getWidth();
            size.y = d.getHeight();
        }
        return size;
    }

}
