package v2.simpleUi;

import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import dagger.ObjectGraph;

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
	private HashMap<String, Object> transferList;
	private ObjectGraph objectGraph;
	private static Context context;

	public HashMap<String, Object> getTransferList() {
		if (transferList == null) {
			transferList = new HashMap<String, Object>();
		}
		return transferList;
	}

	/**
	 * To use square.github.io/dagger for injection do the following: <br>
	 * <br>
	 * Goto Project->Properties->Java Compiler-> and check
	 * "Enable project specific settings". <br>
	 * <br>
	 * AND Goto Project->->Properties->Java Compiler->Factory path and check
	 * "Enable project specific settings" Under Factory path Click add JARs and
	 * add dagger-compiler-.jar, dagger-.jar, javawriter-.jar and
	 * javax.inject.jar (order does not matter)
	 * 
	 * 
	 * @param modules
	 *            you only have to pass this the first time you call this
	 *            method, pass all Modules here
	 * @return will always return the same object graph instance
	 */
	public ObjectGraph getObjectGraph(Object... modules) {
		if (objectGraph == null) {
			if (modules == null || modules.length <= 0) {
				Log.e(LOG_TAG, "The first time you call this method you "
						+ "have to pass all module classes which "
						+ "handle what to inject on the runtime");
			}
			objectGraph = ObjectGraph.create(modules);
		}
		return objectGraph;
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
		getTransferList().put(newKey, object);
		return newKey;
	}

}
