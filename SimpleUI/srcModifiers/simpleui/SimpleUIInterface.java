package simpleui;

import simpleui.modifiers.ModifierInterface;
import android.view.View;

public interface SimpleUIInterface {

	void setMyModifier(ModifierInterface myModifier);

	void setMyViewToShow(View myViewToShow);

	View getMyViewToShow();

	ModifierInterface getMyModifier();

	/**
	 * on default does nothing
	 * 
	 * Override this method in your subclass to get all the features of the
	 * {@link SimpleUI} class but with a static content
	 * 
	 * @return a {@link View} or a {@link ModifierInterface} to be displayed
	 */
	Object loadStaticElementToDisplay();

}
