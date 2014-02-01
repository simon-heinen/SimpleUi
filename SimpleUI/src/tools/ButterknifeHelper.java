package tools;

import android.app.Activity;
import android.util.Log;
import butterknife.Views;

public class ButterknifeHelper {

	private static final String LOG_TAG = "Butterknife";

	public static void load(Activity a, int R_layout_yourXML) {
		a.setContentView(R_layout_yourXML);
		Views.inject(a);// butterknife injection
		String className = a.getClass().getCanonicalName() + "$$ViewInjector";
		try {
			Log.i(LOG_TAG, "Checking if butterknife injections exist"
					+ ", className=" + className);
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			// injection did not work show instructions:
			Log.e(LOG_TAG, "! Butterknife could not find injection classes "
					+ "(see http://jakewharton.github.io/"
					+ "butterknife/ide-eclipse.html )");
			Log.e(LOG_TAG, "! Right click on project -> "
					+ "Head to Java Compiler -> Annotation "
					+ "Processing -> CHECK 'Enable project "
					+ "specific settings'");
			Log.e(LOG_TAG, "! Select 'Factory Path' -> "
					+ "Check \"Enable project specific settings\" "
					+ "-> click \"Add JARs…\" -> Navigate to the "
					+ "project's libs/ folder and select "
					+ "the Butter Knife jar -> Click Ok");
			throw new RuntimeException("Butterknife could not "
					+ "find injection class '" + className + "'");
		}
	}
}
