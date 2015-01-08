package simpleui.modifiers;

import android.app.Activity;
import android.view.View;

public interface ModifierV2<T extends ModifierV2> {

	/**
	 * generates the android UI for the modifier
	 * 
	 * @param activity
	 * @param parent
	 *            the parent or null if no parent exists
	 * @return
	 */
	View getView(Activity activity, T parent);

	/**
	 * @param activity
	 * @return true if the save procedure was successful
	 */
	boolean save(Activity activity);

	/**
	 * @param activity
	 * @return true if cancel is allowed
	 */
	boolean cancel(Activity activity);

}