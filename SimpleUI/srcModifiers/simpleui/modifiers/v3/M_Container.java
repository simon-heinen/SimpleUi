package simpleui.modifiers.v3;

import simpleui.SimpleUI.OptionsMenuListener;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.ExampleDecorator;
import simpleui.util.Log;
import simpleui.util.MenuItemList;
import simpleui.util.SimpleUiApplication;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class M_Container extends M_Collection implements OptionsMenuListener {

	private static final String LOG_TAG = M_Container.class.getSimpleName();
	private static final int OUTER_BACKGROUND_DIMMING_COLOR = Color.argb(200,
			255, 255, 255);

	private Context context;
	MenuItemList menuItemList;
	private Integer cardBackgroundColor = null;
	private boolean fillCompleteScreen = false;

	@Override
	public View getView(Context context) {
		this.context = context;
		LinearLayout mostOuterBox = new LinearLayout(context);
		mostOuterBox.setGravity(Gravity.CENTER);
		mostOuterBox.setBackgroundColor(OUTER_BACKGROUND_DIMMING_COLOR);

		LinearLayout outerContainer = new LinearLayout(context);
		LinearLayout listItemContainer = new LinearLayout(context);
		boolean firstEntryIsToolbar = get(0) instanceof M_Toolbar;
		boolean fillScreen = firstEntryIsToolbar || this.fillCompleteScreen;

		int shaddowSize = M_CardView.DEFAULT_SHADDOW_SIZE;
		if (fillScreen) {
			shaddowSize = 0;
		}
		CardView card = M_CardView.newCardViewWithContainers(context,
				outerContainer, listItemContainer, shaddowSize);
		if (cardBackgroundColor != null) {
			card.setCardBackgroundColor(cardBackgroundColor);
		}
		createViewsForAllModifiers(context, listItemContainer,
				firstEntryIsToolbar);
		if (fillScreen) {
			card.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
		}
		if (firstEntryIsToolbar) {
			outerContainer.addView(get(0).getView(context), 0);
		}
		mostOuterBox.addView(card);
		return mostOuterBox;
	}

	public void setFillCompleteScreen(boolean fillCompleteScreen) {
		this.fillCompleteScreen = fillCompleteScreen;
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

	public void setCardBackgroundColor(Integer cardBackgroundColor) {
		this.cardBackgroundColor = cardBackgroundColor;
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
