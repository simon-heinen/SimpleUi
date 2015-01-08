package simpleui.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
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
			Log.e(LOG_TAG, "! Eclipse users: Right click on project -> "
					+ "Head to Java Compiler -> Annotation "
					+ "Processing -> CHECK 'Enable project "
					+ "specific settings'");
			Log.e(LOG_TAG, "! Select 'Factory Path' -> "
					+ "Check \"Enable project specific settings\" "
					+ "-> click \"Add JARs\" -> Navigate to the "
					+ "project's libs/ folder and select "
					+ "the Butter Knife jar -> Click Ok");
			throw new RuntimeException("Butterknife could not "
					+ "find injection class '" + className + "'");
		}
	}

	/**
	 * @param context
	 * @param id
	 * @return
	 */
	public static int getViewIdViaIdAsString(Context context, String id) {
		return context.getResources().getIdentifier(id, "id",
				context.getPackageName());
	}

	public static View injectFieldsInListItem(Context context, Object listItem,
			View convertView, int listItemLayoutId) {
		convertView = inflateViewFromXml(context, convertView, listItemLayoutId);
		Views.inject(listItem, convertView);
		return convertView;
	}

	public static View inflateViewFromXml(Context context, View convertView,
			int listItemLayoutId) {
		if (convertView == null || convertView.getId() != listItemLayoutId) {
			convertView = View.inflate(context, listItemLayoutId, null);
			convertView.setId(listItemLayoutId);
		}
		return convertView;
	}
}
