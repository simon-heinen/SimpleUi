package v4;

import android.content.Context;
import android.view.View;

public interface ModifierInterfaceV2 {

	/**
	 * generates the android UI for the modifier
	 * 
	 * @param context
	 * @return
	 */
	View getView(Context context);

	/**
	 * @param context
	 * @return true if the save procedure was successful
	 */
	boolean save(Context context);

	/**
	 * @param context
	 * @return true
	 */
	boolean cancel(Context context);

}