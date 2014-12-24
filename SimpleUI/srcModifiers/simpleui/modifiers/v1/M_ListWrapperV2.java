package simpleui.modifiers.v1;

import java.util.ArrayList;
import java.util.Collection;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_ButtonBorderless;
import simpleui.modifiers.v3.M_LeftRight;
import simpleui.modifiers.v3.M_TextModifier;
import android.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * generates a list container structure where new elements can be added and
 * existing ones can be removed
 */
public abstract class M_ListWrapperV2<T> implements ModifierInterface {

	private static final String LOG_TAG = "M_ListWrapperV2";
	private ArrayList<WrapperItem<T>> items;
	private String addItemButtonText;
	private LinearLayout linLayContainer;
	private int leftSize = 4;
	private int rightSize = 1;

	public static class WrapperItem<T> {
		public WrapperItem(T item) {
			this.item = item;
		}

		public T item;
		public ModifierInterface modifier;
		public View view;
		public boolean removeRequest;
		public boolean isNewItem;
	}

	public M_ListWrapperV2(Collection<T> initialList, String addButtonText,
			int leftSize, int rightSize) {
		this(initialList, addButtonText);
		this.leftSize = leftSize;
		this.rightSize = rightSize;
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
		if (initialList != null) {
			for (T item : initialList) {
				items.add(new WrapperItem<T>(item));
			}
		}
	}

	/**
	 * @return the temporary list while the modifier is shown, which contains
	 *         all elements (also the removed ones from this session) together
	 *         with their modifiers and generated views
	 */
	public ArrayList<WrapperItem<T>> getItems() {
		return items;
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
		for (int posInList = 0; posInList < items.size(); posInList++) {
			WrapperItem<T> iw = items.get(posInList);
			if (iw.modifier == null || iw.view == null) {
				iw.modifier = getModifierForItem(context, iw.item, posInList);
				iw.view = generateNewViewForItem(context, iw, items, posInList);
			}
			if (!iw.removeRequest) {
				linLayContainer.addView(iw.view);
			}
		}
	}

	public View generateNewViewForItem(Context context,
			final WrapperItem<T> itemWrapper,
			ArrayList<WrapperItem<T>> itemList, int posInList) {
		M_LeftRight h = new M_LeftRight(itemWrapper.modifier, leftSize,
				new M_ButtonBorderless(R.drawable.ic_delete) {

					@Override
					public void onClick(Context context, View clickedButton) {
						if (itemWrapper.isNewItem) {
							/*
							 * if its a new item it is not yet in the original
							 * list and can be removed instantly
							 */
							items.remove(itemWrapper);
						} else {
							/*
							 * mark it as "to be removed" to inform the real
							 * list to remove it when the save action is
							 * executed
							 */
							itemWrapper.removeRequest = true;
						}
						refreshListContent(context);
					}

				}, rightSize);
		return h.getView(context);
	}

	/**
	 * This method is used to create an "Add" Button by
	 * {@link #getView(Context)}. The default implementation returns a
	 * {@link M_Button} which, on click, queries
	 * {@link #getNewItemInstance(Context, int)} for a new object and adds it to
	 * the list.<br>
	 * <br>
	 * Subclasses can (must don't have to) override it. Implementations can call
	 * the method {@link #addItemToWrapperList(Context, Object)}, e.g. when they
	 * create a button that opens a list with objects to choose from.
	 * 
	 * @param context
	 * @return A {@link M_Button} that adds a new
	 *         {@link #getNewItemInstance(Context, int)}.
	 */
	protected ModifierInterface createAddButton(Context context) {
		return new M_Button(addItemButtonText) {
			@Override
			public void onClick(Context context, Button clickedButton) {
				T item = getNewItemInstance(context, items.size());
				addItemToWrapperList(context, item);
			}
		};
	}

	/**
	 * Can be called by overriding implementations of
	 * {@link #createAddButton(Context)}.
	 * 
	 * @param context
	 * @param item
	 */
	protected final void addItemToWrapperList(Context context, T item) {
		if (item != null) {
			WrapperItem<T> iw = new WrapperItem<T>(item);
			iw.isNewItem = true;
			items.add(iw);
			refreshListContent(context);
		} else {
			Log.w(LOG_TAG, "New item instance was null");
		}
	}

	protected void refreshListContent(Context context) {
		generateList(context, linLayContainer);
		linLayContainer.invalidate();
	}

	@Override
	public boolean save() {

		// first request to remove the marked elements
		int pos = 0;

		if (items.size() > 0) { // Else OutOfBoundsException
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
		}
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
	 * return a modifier for the passed item and also implement the save action
	 * as usual. the save action will only be executed when the complete list is
	 * saved
	 * 
	 * @param c
	 * @param item
	 * @param posInList
	 * @return a modifier for the item
	 */
	public abstract ModifierInterface getModifierForItem(Context c, T item,
			int posInList);

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

	public static M_ListWrapperV2<String> newStringCollectionModifier(
			final Collection<String> list, String buttonText) {
		if (list == null) {
			Log.e(LOG_TAG, "Can't pass a null object for the target list");
			return null;
		}
		return new M_ListWrapperV2<String>(list, buttonText) {
			@Override
			public ModifierInterface getModifierForItem(Context c,
					final String item, int posInList) {
				return new M_TextModifier() {

					@Override
					public boolean save(String newValue) {
						list.remove(item);
						list.add(newValue);
						return true;
					}

					@Override
					public String load() {
						return item;
					}

					@Override
					public String getVarName() {
						return null;
					}
				};
			}

			@Override
			public String getNewItemInstance(Context c, int posOfNewItemInList) {
				return "";
			}

			@Override
			public boolean onRemoveRequest(String item) {
				if (!list.remove(item)) {
					Log.w(LOG_TAG, "Item " + item
							+ " to remove was not in the target list!");
				}
				return true;
			}

			@Override
			public boolean onAddRequest(String item) {
				return list.add(item);
			}

		};
	}

}
