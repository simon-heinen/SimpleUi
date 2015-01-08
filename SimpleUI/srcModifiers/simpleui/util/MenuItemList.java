package simpleui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import simpleui.SimpleUI.OptionsMenuListener;
import simpleui.util.MenuItemList.MItem;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Helper class to define menu items in a simple way with an OO design for the
 * onClick handling
 */
public class MenuItemList extends ArrayList<MItem> implements
		OptionsMenuListener {

	private static final long serialVersionUID = 1L;
	private static final String LOG_TAG = MenuItemList.class.getSimpleName();

	public static abstract class MItem {

		private final int order = Menu.NONE;
		private final int groupId = Menu.NONE;
		private final String itemTitle;
		private Drawable icon;
		private Integer iconId;
		private MenuItem item;

		public MItem(String itemTitle) {
			this.itemTitle = itemTitle;
		}

		public MItem(String itemTitle, Drawable icon) {
			this.itemTitle = itemTitle;
			this.icon = icon;
		}

		public MItem(String itemTitle, int iconId) {
			this.itemTitle = itemTitle;
			this.iconId = iconId;
		}

		public abstract void onClick(Activity activity);
	}

	private Menu menu;
	private final Map<MenuItem, MItem> map = new HashMap<MenuItem, MenuItemList.MItem>();

	@Override
	public boolean onCreateOptionsMenu(Activity a, Menu m) {
		menu = m;
		if (size() > 0) {
			return generateMenu();
		}
		return false;
	}

	private boolean generateMenu() {
		if (menu == null) {
			Log.w(LOG_TAG, "Won't generate menu, menu not yet set");
			return false;
		}
		map.clear();
		for (MItem e : this) {
			MenuItem item = menu
					.add(e.groupId, Menu.NONE, e.order, e.itemTitle);
			if (e.iconId != null) {
				item.setIcon(e.iconId);
			}
			if (e.icon != null) {
				item.setIcon(e.icon);
			}
			e.item = item;
			map.put(item, e);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(Activity a, MenuItem item) {
		MItem e = map.get(item);
		if (e != null) {
			e.onClick(a);
			return true;
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Activity a, Menu menu) {
		return true;
	}

	@Override
	public void onOptionsMenuClosed(Activity a, Menu menu) {
	}

}
