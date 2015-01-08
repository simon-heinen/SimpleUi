package simpleui.modifiers.v3;

import java.util.LinkedHashSet;
import java.util.List;

import simpleui.modifiers.ModifierInterface;
import android.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * use the M_ListWrapperV2 instead
 * 
 * @param <T>
 */
@Deprecated
public abstract class M_ListWrapper<T> implements ModifierInterface {

	private class ModifierAndView<T> {

		View view;
		ModifierInterface modifier;
		T item;

		public ModifierAndView(ModifierInterface m, View v, T i) {
			view = v;
			modifier = m;
			item = i;
		}

		public View getView() {
			return view;
		}

		public ModifierInterface getModifier() {
			return modifier;
		}

		public T getItem() {
			return item;
		}

	}

	private static final String LOG_TAG = "M_ListWrapper";

	private final List<T> passedList;
	private final String addItemText;
	private LinearLayout linLayContainer;
	private final LinkedHashSet<ModifierAndView<T>> hashmap = new LinkedHashSet<ModifierAndView<T>>();

	@Deprecated
	public M_ListWrapper(List<T> list, String addItemText) {
		passedList = list;
		this.addItemText = addItemText;
	}

	@Override
	public View getView(Context context) {
		linLayContainer = new LinearLayout(context);
		linLayContainer.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		linLayContainer.setOrientation(LinearLayout.VERTICAL);
		generateList(context);
		return linLayContainer;
	}

	public void generateList(Context context) {
		linLayContainer.removeAllViews();
		if (addItemText != null) {
			linLayContainer.addView(createAddButton(context).getView(context));
		}
		if (passedList != null) {
			for (final T item : passedList) {
				int lw = 3;
				int rw = 1;

				ModifierAndView<T> listElement = hashmapget(item);
				if (listElement == null) {

					ModifierInterface modifierForItem = getModifierFor(item);
					M_LeftRight h = new M_LeftRight(modifierForItem, lw,
							new M_ButtonBorderless(R.drawable.ic_delete) {

								@Override
								public void onClick(Context context,
										View clickedButton) {
									onDelete(item);
									refreshListContent(context);
								}

							}, rw);
					listElement = new ModifierAndView<T>(modifierForItem,
							h.getView(context), item);
					hashmap.add(listElement);
				}
				LinearLayout par = (LinearLayout) listElement.getView()
						.getParent();
				if (par != null) {
					Log.w(LOG_TAG, "view for " + listElement.getModifier()
							+ " had already a parent!");
					par.removeView(listElement.getView());
				}
				linLayContainer.addView(listElement.getView());
			}
		}
	}

	private ModifierAndView<T> hashmapget(T e) {
		for (ModifierAndView<T> m : hashmap) {
			if (m.getItem() == e) {
				return m;
			}
		}
		return null;
	}

	public void refreshListContent(Context context) {
		generateList(context);
		linLayContainer.invalidate();
	}

	private ModifierInterface createAddButton(Context context) {
		return new M_Button(addItemText) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				if (addNewObjectToList(context, passedList)) {
					refreshListContent(context);
				}

			}

		};
	}

	public abstract boolean addNewObjectToList(Context context, List<T> list);

	public abstract ModifierInterface getModifierFor(T item);

	/**
	 * delete the passed item from the list and return true
	 * 
	 * @param item
	 * @return
	 */
	public abstract boolean onDelete(T item);

	@Override
	public boolean save() {
		for (T e : passedList) {
			if (!hashmapget(e).getModifier().save()) {
				return false;
			}
		}
		return true;
	}

}
