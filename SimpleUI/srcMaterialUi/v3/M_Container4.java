package v3;

import v2.simpleUi.M_Collection;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.SimpleUI.OptionsMenuListener;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class M_Container4 extends M_Collection implements OptionsMenuListener {

	@Override
	public View getView(Context context) {

		LinearLayout mostOuterBox = new LinearLayout(context);
		mostOuterBox.setGravity(Gravity.CENTER);
		// mostOuterBox.setBackgroundColor(Color.RED);

		CardView card = new CardView(context);

		card.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
		int shaddowSize = 35;
		card.setMaxCardElevation(shaddowSize);
		card.setCardElevation(shaddowSize);

		// container for header, list of items and footer:
		LinearLayout cardLin = new LinearLayout(context);
		cardLin.setOrientation(LinearLayout.VERTICAL);

		ScrollView scrollContainer = new ScrollView(context);

		LinearLayout l = new LinearLayout(context);
		l.setOrientation(LinearLayout.VERTICAL);
		boolean firstEntryIsToolbar = get(0) instanceof M_Toolbar;
		createViewsForAllModifiers(context, l, firstEntryIsToolbar);

		scrollContainer.addView(l);
		if (firstEntryIsToolbar) {
			cardLin.addView(get(0).getView(context));
		}
		cardLin.addView(scrollContainer);
		card.addView(cardLin);
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

	MenuItemList menuItemList;

	public void setMenuItemList(MenuItemList menuItemList) {
		this.menuItemList = menuItemList;
	}

	@Override
	public boolean onCreateOptionsMenu(Activity a, Menu menu) {
		if (menuItemList != null) {
			return menuItemList.onCreateOptionsMenu(a, menu);
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Activity a, Menu menu) {
		if (menuItemList != null) {
			return menuItemList.onPrepareOptionsMenu(a, menu);
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(Activity a, MenuItem item) {
		if (menuItemList != null) {
			return menuItemList.onOptionsItemSelected(a, item);
		}
		return false;
	}

	@Override
	public void onOptionsMenuClosed(Activity a, Menu menu) {
		menuItemList.onOptionsMenuClosed(a, menu);
	}
}
