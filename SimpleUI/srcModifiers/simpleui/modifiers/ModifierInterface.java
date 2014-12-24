package simpleui.modifiers;

import android.content.Context;
import android.view.View;

public interface ModifierInterface {

	public static final int DEFAULT_PADDING = 4;

	/**
	 * generates the android UI for the modifier
	 * 
	 * @param context
	 * @return
	 */
	public abstract View getView(Context context);

	/**
	 * @return true if the save procedure was successful
	 */
	public abstract boolean save();

}