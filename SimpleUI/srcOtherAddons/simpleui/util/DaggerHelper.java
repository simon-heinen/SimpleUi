package simpleui.util;

import android.util.Log;
import dagger.ObjectGraph;

public class DaggerHelper {
	private static final String LOG_TAG = "DaggerHelper";
	private static ObjectGraph objectGraph;

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
	public static ObjectGraph getObjectGraph(Object... modules) {
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
}
