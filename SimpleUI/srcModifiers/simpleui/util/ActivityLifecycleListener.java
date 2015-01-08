package simpleui.util;

import simpleui.SimpleUI;
import simpleui.modifiers.ModifierInterface;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * It is possible to listen to the onActivityResult from any
 * {@link ModifierInterface} class you want, just implement this interface as
 * well, pass the {@link ModifierInterface} to the
 * {@link SimpleUI#showUi(Context, ModifierInterface)} e.g. and it will be
 * notified when the {@link SimpleUI} gets an onActivityResult event
 * 
 * @author Simon Heinen
 * 
 */
public interface ActivityLifecycleListener {
	/**
	 * read also {@link Activity#onActivityResult}
	 * 
	 * @param a
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	void onActivityResult(Activity a, int requestCode, int resultCode, Intent data);

	/**
	 * called when the activity is no longer visible
	 * 
	 * @param activity
	 */
	void onStop(Activity activity);

	/**
	 * @param a
	 * @return true if the activity should be allowed to close
	 */
	boolean onCloseWindowRequest(Activity activity);

	/**
	 * Called when another activity comes into foreground
	 * 
	 * @param activity
	 */
	void onPause(Activity activity);

	/**
	 * called as the last event before the activity is shown
	 * 
	 * @param activity
	 */
	void onResume(Activity activity);
}
