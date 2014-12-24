package simpleui.modifiers.v1.uiDecoration;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.util.ColorUtils;
import simpleui.util.TextUtils;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * For simple decoration use the util classes like {@link ColorUtils} and
 * {@link TextUtils}
 * 
 * @author Simon Heinen
 * 
 */
public interface UiDecorator {

	public static final int TYPE_CONTAINER = 1;
	public static final int TYPE_INFO_TEXT = 2;
	public static final int TYPE_EDIT_TEXT = 3;
	public static final int TYPE_BUTTON = 4;
	public static final int TYPE_ICON = 5;
	public static final int TYPE_CAPTION = 6;

	/**
	 * @param context
	 * @param targetView
	 *            The view that will be decorated
	 * @param level
	 *            the deep in the UI tree, normally pass
	 *            {@link UiDecorator#getCurrentLevel()}+x here, and x is the
	 *            internal deep in the current {@link UiDecoratable}. See the
	 *            implementation of {@link ModifierInterface}s like
	 *            {@link M_Button} for example implementations
	 * @param type
	 *            The type of the view (e.g. {@link UiDecorator#TYPE_BUTTON} if
	 *            the view is a (sub)type of {@link Button})
	 * @return
	 */
	public boolean decorate(Context context, View targetView, final int level,
			int type);

	/**
	 * @param context
	 * @param targetView
	 *            The view that will be decorated
	 * @param level
	 *            the deep in the UI tree, normally pass
	 *            {@link UiDecorator#getCurrentLevel()}+x here, and x is the
	 *            internal deep in the current {@link UiDecoratable}. See the
	 *            implementation of {@link ModifierInterface}s like
	 *            {@link M_Button} for example implementations
	 * @param type
	 *            The type of the view (e.g. {@link UiDecorator#TYPE_BUTTON} if
	 *            the view is a (sub)type of {@link Button})
	 * @return
	 */
	public boolean decorate(Context context, Button targetView,
			final int level, int type);

	/**
	 * @param context
	 * @param targetView
	 *            The view that will be decorated
	 * @param level
	 *            the deep in the UI tree, normally pass
	 *            {@link UiDecorator#getCurrentLevel()}+x here, and x is the
	 *            internal deep in the current {@link UiDecoratable}. See the
	 *            implementation of {@link ModifierInterface}s like
	 *            {@link M_Button} for example implementations
	 * @param type
	 *            The type of the view (e.g. {@link UiDecorator#TYPE_BUTTON} if
	 *            the view is a (sub)type of {@link Button})
	 * @return
	 */
	public boolean decorate(Context context, TextView targetView,
			final int level, int type);

	/**
	 * @param context
	 * @param targetView
	 *            The view that will be decorated
	 * @param level
	 *            the deep in the UI tree, normally pass
	 *            {@link UiDecorator#getCurrentLevel()}+x here, and x is the
	 *            internal deep in the current {@link UiDecoratable}. See the
	 *            implementation of {@link ModifierInterface}s like
	 *            {@link M_Button} for example implementations
	 * @param type
	 *            The type of the view (e.g. {@link UiDecorator#TYPE_BUTTON} if
	 *            the view is a (sub)type of {@link Button})
	 * @return
	 */
	public boolean decorate(Context context, ImageView targetView,
			final int level, int type);
	
	/**
	 * @param context
	 * @param targetView
	 *            The view that will be decorated
	 * @param level
	 *            the deep in the UI tree, normally pass
	 *            {@link UiDecorator#getCurrentLevel()}+x here, and x is the
	 *            internal deep in the current {@link UiDecoratable}. See the
	 *            implementation of {@link ModifierInterface}s like
	 *            {@link M_Button} for example implementations
	 * @param type
	 *            The type of the view (e.g. {@link UiDecorator#TYPE_BUTTON} if
	 *            the view is a (sub)type of {@link Button})
	 * @return
	 */
	public boolean decorate(Context context, EditText targetView,
			final int level, int type);

	public int getCurrentLevel();

	public void setCurrentLevel(int currentLevel);

}
