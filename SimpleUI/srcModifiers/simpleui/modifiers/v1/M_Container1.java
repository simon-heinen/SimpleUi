package simpleui.modifiers.v1;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_Collection;
import simpleui.util.ColorUtils;
import simpleui.util.SimpleUiApplication;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

@Deprecated
public class M_Container1 extends M_Collection implements UiDecoratable {

	private static final int MOST_OUTER_PADDING = 13;
	private static final int OUTER_BACKGROUND_DIMMING_COLOR = android.graphics.Color
			.argb(200, 0, 0, 0);
	private static final ColorUtils BACKGROUND = ColorUtils.newGrayBackground();
	private static final String LOG_TAG = "M_Container";
	private UiDecorator myDecorator;
	private Context context;

	@Override
	public View getView(Context target) {
		this.context = target;
		LinearLayout containerForAllItems = new LinearLayout(target);
		ScrollView scrollContainer = new ScrollView(target);
		LinearLayout mostOuterBox = new LinearLayout(target);

		LayoutParams layParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);

		containerForAllItems.setLayoutParams(layParams);
		containerForAllItems.setPadding(MOST_OUTER_PADDING, MOST_OUTER_PADDING,
				MOST_OUTER_PADDING, MOST_OUTER_PADDING);
		containerForAllItems.setOrientation(LinearLayout.VERTICAL);

		scrollContainer.setLayoutParams(layParams);
		scrollContainer.addView(containerForAllItems);

		mostOuterBox.setGravity(Gravity.CENTER);
		mostOuterBox.setBackgroundColor(OUTER_BACKGROUND_DIMMING_COLOR);
		mostOuterBox.setPadding(MOST_OUTER_PADDING, MOST_OUTER_PADDING,
				MOST_OUTER_PADDING, MOST_OUTER_PADDING);
		mostOuterBox.addView(scrollContainer);

		setWindowBackgroundColor(target, scrollContainer);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(target, mostOuterBox, level + 1,
					UiDecorator.TYPE_CONTAINER);
			myDecorator.decorate(target, scrollContainer, level + 2,
					UiDecorator.TYPE_CONTAINER);
			myDecorator.setCurrentLevel(level + 3);
		}

		createViewsForAllModifiers(target, containerForAllItems);

		if (myDecorator != null) {
			/*
			 * Then reduce level again to the previous value
			 */
			myDecorator.setCurrentLevel(myDecorator.getCurrentLevel() - 3);
		}

		return mostOuterBox;
	}

	public Context getContext() {
		if (context == null) {
			context = SimpleUiApplication.getContext();
		}
		return context;
	}

	protected void setWindowBackgroundColor(Context c,
			ScrollView scrollContainer) {

		// int id = 0;
		// id = c.getResources().getIdentifier("simpleUiLinLayBackground",
		// "values", this.getClass().getPackage().getName());
		//
		// int attrsResourceIdArray[] = { id };
		// int custom_text_offset = 0;
		// TypedArray t = c.obtainStyledAttributes(attrsResourceIdArray);
		// String colorToUse = t.getString(custom_text_offset);

		if (scrollContainer.getBackground() == null) {
			BACKGROUND.applyBackgroundTo(scrollContainer);
		}
	}

	@Override
	public String toString() {
		if (!isEmpty()) {
			if (get(0).getClass() == M_Caption.class
					|| get(0).getClass().isAssignableFrom(M_Caption.class)
					|| M_Caption.class.isAssignableFrom(get(0).getClass())) {
				return "Screen " + get(0).toString();
			} else {
				String cl = "";
				for (ModifierInterface m : this) {
					cl += m.getClass() + ",";
				}
				return "(" + this.size() + ")[" + cl + "]";
			}
		}
		return getClass() + "(0)[]";
	}

	@Override
	public boolean save() {
		for (ModifierInterface m : this) {
			if (m != null) {
				if (!m.save()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		boolean result = true;
		myDecorator = decorator;
		for (ModifierInterface m : this) {
			if (m instanceof UiDecoratable) {
				result &= ((UiDecoratable) m).assignNewDecorator(decorator);
			} else {
				/*
				 * if not all children are UiDecoratables the overall result
				 * will be false
				 */
				result = false;
			}
		}
		return result;
	}

}
