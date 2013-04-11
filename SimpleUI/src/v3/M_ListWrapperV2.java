package v3;

import java.util.ArrayList;
import java.util.Collection;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_IconButtonWithText;
import v2.simpleUi.M_LeftRight;
import v2.simpleUi.ModifierInterface;
import android.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 
 * generates a list container structure where new elements can be added and
 * existing ones can be removed
 * 
 * @author Spobo
 * 
 */
public abstract class M_ListWrapperV2<T> implements ModifierInterface {

	protected static final String LOG_TAG = "M_ListWrapperV2";
	private ArrayList<WrapperItem<T>> items;
	private String addItemButtonText;
	private LinearLayout linLayContainer;

	public static class WrapperItem<T> {
		public WrapperItem(T item) {
			this.item = item;
		}

		T item;
		ModifierInterface modifier;
		View view;
		boolean removeRequest;
		boolean isNewItem;
	}

	/**
	 * @param initialList
	 *            this list will not be modified by the controller
	 * @param addButtonText
	 *            can be null, then no button will be added
	 */
	public M_ListWrapperV2(Collection<T> initialList, String addButtonText) {
		addItemButtonText = addButtonText;
		items = new ArrayList<M_ListWrapperV2.WrapperItem<T>>();
		for (T item : initialList) {
			items.add(new WrapperItem<T>(item));
		}
	}

	@Override
	public View getView(Context context) {
		/*
		 * only generate the views for the modifiers once
		 */
		linLayContainer = new LinearLayout(context);
		linLayContainer.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		linLayContainer.setOrientation(LinearLayout.VERTICAL);
		generateList(context, linLayContainer);
		return linLayContainer;
	}

	private void generateList(Context context, LinearLayout targetContainer) {
		linLayContainer.removeAllViews();
		if (addItemButtonText != null) {
			linLayContainer.addView(createAddButton(context).getView(context));
		}
		for (WrapperItem<T> iw : items) {
			if (iw.modifier == null || iw.view == null) {
				iw.modifier = getModifierForItem(context, iw.item);
				iw.view = generateNewViewForItem(context, iw);
			}
			if (!iw.removeRequest) {
				linLayContainer.addView(iw.view);
			}
		}
	}

	public View generateNewViewForItem(Context context,
			final WrapperItem<T> itemWrapper) {
		M_LeftRight h = new M_LeftRight(itemWrapper.modifier, 3,
				new M_IconButtonWithText(R.drawable.ic_delete) {

					@Override
					public void onClick(Context context, ImageView clickedButton) {
						itemWrapper.removeRequest = true;
						refreshListContent(context);
					}

				}, 1);
		return h.getView(context);
	}

	private ModifierInterface createAddButton(Context context) {
		return new M_Button(addItemButtonText) {

			@Override
			public void onClick(Context context, Button clickedButton) {

				T item = getNewItemInstance(context, items.size());
				if (item != null) {
					WrapperItem<T> iw = new WrapperItem<T>(item);
					iw.isNewItem = true;
					items.add(iw);
					refreshListContent(context);
				} else {
					Log.w(LOG_TAG, "New item instance was null");
				}
			}

		};
	}

	protected void refreshListContent(Context context) {
		generateList(context, linLayContainer);
		linLayContainer.invalidate();
	}

	@Override
	public boolean save() {

		// first request to remove the marked elements
		int pos = 0;
		do {
			WrapperItem<T> iw = items.get(pos);
			if (iw.removeRequest && !iw.isNewItem) {
				if (!onRemoveRequest(iw.item)) {
					return false;
				}

			}
			if (iw.removeRequest) {
				items.remove(pos);
			} else {
				pos++;
			}
		} while (pos < items.size());
		// then save all remaining items and inform on all new items
		for (WrapperItem<T> iw : items) {
			if (iw.removeRequest) {
				Log.e(LOG_TAG, "Found a remaining item which was not removed");
			}
			if (iw.isNewItem && !iw.removeRequest) {
				onAddRequest(iw.item);
			}
			if (!iw.modifier.save()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * return a modifier for the passed item and also implement the save action
	 * as usual. the save action will only be executed when the complete list is
	 * saved
	 * 
	 * @param c
	 * @param item
	 * @return a modifier for the item
	 */
	public abstract ModifierInterface getModifierForItem(Context c, T item);

	/**
	 * @param c
	 * @param posOfNewItemInList
	 *            can be used for initializing the object e.g. if the type is
	 *            String you could return "Item Nr. "+posOfNewItemInList
	 * @return a new initialized instance of the object
	 */
	public abstract T getNewItemInstance(Context c, int posOfNewItemInList);

	/**
	 * will be called before the modifier for the item could save its content
	 * 
	 * @param item
	 * @return false if the item was not removed and its modifier view should
	 *         reapear
	 */
	public abstract boolean onRemoveRequest(T item);

	/**
	 * will be executed after the modifier for the new item saved its content so
	 * just add it to your collection when this event appears
	 * 
	 * @param item
	 * @return
	 */
	public abstract boolean onAddRequest(T item);

}
