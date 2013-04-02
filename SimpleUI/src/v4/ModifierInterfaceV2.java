package v4;

import android.app.Activity;
import android.view.View;

public interface ModifierInterfaceV2 {

	/**
	 * generates the android UI for the modifier
	 * 
	 * @param activity
	 * @param parent
	 *            the parent or null if no parent exists
	 * @return
	 */
	View getView(Activity activity, ModifierInterfaceV2 parent);

	/**
	 * @param activity
	 * @return true if the save procedure was successful
	 */
	boolean save(Activity activity);

	/**
	 * @param activity
	 * @return true
	 */
	boolean cancel(Activity activity);

}