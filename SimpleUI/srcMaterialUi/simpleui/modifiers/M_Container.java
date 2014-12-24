package simpleui.modifiers;

import tools.SimpleUiApplication;
import util.Log;
import v2.simpleUi.M_Collection;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.SimpleUI.OptionsMenuListener;
import v2.simpleUi.uiDecoration.ExampleDecorator;
import v3.MenuItemList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class M_Container extends M_Collection implements OptionsMenuListener {

	private static final String LOG_TAG = M_Container.class.getSimpleName();
	private static final int OUTER_BACKGROUND_DIMMING_COLOR = Color.argb(200,
			255, 255, 255);

	private Context context;
	MenuItemList menuItemList;

	@Override
	public View getView(Context context) {

		LinearLayout mostOuterBox = new LinearLayout(context);
		mostOuterBox.setGravity(Gravity.CENTER);
		mostOuterBox.setBackgroundColor(OUTER_BACKGROUND_DIMMING_COLOR);

		LinearLayout outerContainer = new LinearLayout(context);
		LinearLayout listItemContainer = new LinearLayout(context);
		CardView card = M_CardView.newCardViewWithContainers(context,
				outerContainer, listItemContainer, 0);
		boolean firstEntryIsToolbar = get(0) instanceof M_Toolbar;
		createViewsForAllModifiers(context, listItemContainer,
				firstEntryIsToolbar);
		if (firstEntryIsToolbar) {
			outerContainer.addView(get(0).getView(context), 0);
		}
		mostOuterBox.addView(card);
		return mostOuterBox;
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (ModifierInterface m : this) {
			if (m != null) {
				result &= m.save();
			}
		}
		return result;
	}

	public void setMenuItemList(MenuItemList menuItemList) {
		this.menuItemList = menuItemList;
	}

	@Override
	public boolean onCreateOptionsMenu(Activity a, Menu menu) {
		Log.i(LOG_TAG, "onCreateOptionsMenu menuItemList=" + menuItemList);
		if (menuItemList != null) {
			return menuItemList.onCreateOptionsMenu(a, menu);
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Activity a, Menu menu) {
		Log.i(LOG_TAG, "onPrepareOptionsMenu menuItemList=" + menuItemList);
		if (menuItemList != null) {
			return menuItemList.onPrepareOptionsMenu(a, menu);
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(Activity a, MenuItem item) {
		Log.i(LOG_TAG, "onOptionsItemSelected menuItemList=" + menuItemList);
		if (menuItemList != null) {
			return menuItemList.onOptionsItemSelected(a, item);
		}
		return false;
	}

	@Override
	public void onOptionsMenuClosed(Activity a, Menu menu) {
		menuItemList.onOptionsMenuClosed(a, menu);
	}

	public Context getContext() {
		if (context == null) {
			context = SimpleUiApplication.getContext();
		}
		return context;
	}

	@Deprecated
	public void assignNewDecorator(ExampleDecorator exampleDecorator) {
		Log.e(LOG_TAG, "assignNewDecorator: Decorators are no longer supported");
	}
}
