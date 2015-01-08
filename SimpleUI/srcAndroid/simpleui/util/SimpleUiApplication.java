package simpleui.util;

import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * You need to add <br>
 * android:name="v2.simpleUi.SimpleUiApplication"<br>
 * in the application tag in the manifest.
 * 
 * 
 * 
 * ((SimpleUiApplication) a.getApplication()).getTransfairList().put( newKey,
 * itemToDisplay);
 * 
 */
// TODO change "extends Application" to "extends MultiDexApplication"
public class SimpleUiApplication extends Application {
	private static final String LOG_TAG = "SimpleUiApplication";
	private HashMap<String, Object> transferList;

	private static Context context;

	public HashMap<String, Object> getTransferList() {
		if (transferList == null) {
			transferList = new HashMap<String, Object>();
		}
		return transferList;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SimpleUiApplication.context = getApplicationContext();
	}

	public static void setContext(Context context) {
		if (context != null) {
			SimpleUiApplication.context = context;
		} else {
			Log.w(LOG_TAG,
					"setContext: Tried to set context to null, was ignored");
		}
	}

	public static Context getContext() {
		if (context == null) {
			Log.w(LOG_TAG, "context was null, your application should "
					+ "specify to extend the "
					+ "SimpleUiApplication in the manifest.xml!");
		}
		return context;
	}

	public static SimpleUiApplication getApplication(Activity a) {
		try {
			SimpleUiApplication app = ((SimpleUiApplication) a.getApplication());
			return app;
		} catch (Exception e) {
			Log.v(LOG_TAG, "The used android.app.Application was not a "
					+ "SimpleUiApplication. Please "
					+ "change this in the manifest!");
		}
		return null;
	}

	/**
	 * @param object
	 * @return the key for the new object
	 */
	public String addToTransferList(Object object) {
		String newKey = new Date().toString() + object.toString();
		Object oldObject = getTransferList().remove(newKey);
		if (oldObject != null) {
			Log.w(LOG_TAG, "Old object with same key >" + newKey
					+ "< was deleted: " + oldObject);
		}
		getTransferList().put(newKey, object);
		return newKey;
	}

}
