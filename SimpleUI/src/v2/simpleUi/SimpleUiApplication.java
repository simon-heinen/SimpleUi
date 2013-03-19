package v2.simpleUi;

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
 * 
 * 
 * 
 * @author Simon Heinen
 * 
 */
public class SimpleUiApplication extends Application {
	private static final String LOG_TAG = "SimpleUiApplication";
	private HashMap<String, Object> transfairList;
	private static Context context;

	public HashMap<String, Object> getTransfairList() {
		if (transfairList == null) {
			transfairList = new HashMap<String, Object>();
		}
		return transfairList;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = getApplicationContext();
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
	public String addToTransfairList(Object object) {
		String newKey = new Date().toString() + object.toString();
		getTransfairList().put(newKey, object);
		return newKey;
	}

}
