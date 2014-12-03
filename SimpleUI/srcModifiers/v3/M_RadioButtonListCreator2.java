package v3;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import v2.simpleUi.M_Button;
import v2.simpleUi.ModifierInterface;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class M_RadioButtonListCreator2 implements ModifierInterface {

	private static final String TAG = "M_SpinnerWithCheckboxesCreator";

	private ArrayList<String> itemList;

	private final int layoutWeightSelect = 1;
	private final int layoutWeightTextInput = 2;
	private final int layoutWeightDelete = 1;

	/**
	 * The positions in the item list for all objects that have to be removed
	 */
	private List<Integer> itemPosToRemove;
	private Integer selectedItemNr;
	private LinearLayout listView;

	@Override
	public View getView(final Context context) {
		LinearLayout outerLinLay = new LinearLayout(context);
		outerLinLay.setOrientation(LinearLayout.VERTICAL);

		listView = new LinearLayout(context);
		listView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		listView.setOrientation(LinearLayout.VERTICAL);
		itemList = getItemList();
		selectedItemNr = getSelectedItemNr();
		itemPosToRemove = new ArrayList<Integer>();
		for (int i = 0; i < itemList.size(); i++) {
			final String item = itemList.get(i);
			LinearLayout row = createOptionRow(context, i, item);
			listView.addView(row, new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		}

		outerLinLay.addView(listView);
		outerLinLay.addView(new M_Button(getAddItemButtonText()) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				addNewEmptyItem(context);
				this.getView(context).invalidate();
			}
		}.getView(context));
		return outerLinLay;
	}

	public abstract Integer getSelectedItemNr();

	/**
	 * @return something like "Add new element"
	 */
	public abstract String getAddItemButtonText();

	public void addNewEmptyItem(Context context) {
		LinearLayout emptyItem = createOptionRow(context,
				listView.getChildCount(), null);
		listView.addView(emptyItem, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		emptyItem.requestFocus();
	}

	private LinearLayout createOptionRow(final Context context,
			final int itemNrInList, String itemText) {
		LinearLayout buttonBox = new LinearLayout(context);
		buttonBox.setOrientation(LinearLayout.HORIZONTAL);
		final ImageButton iconView = new ImageButton(context);
		iconView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedItemNr != itemNrInList) {
					selectedItemNr = itemNrInList;
					setAllButtonsToUnChecked(listView);
					iconView.setImageResource(android.R.drawable.radiobutton_on_background);
				}
			}
		});
		if (selectedItemNr == itemNrInList) {
			iconView.setImageResource(android.R.drawable.radiobutton_on_background);
		} else {
			iconView.setImageResource(android.R.drawable.radiobutton_off_background);
		}
		iconView.setBackgroundColor(Color.TRANSPARENT);
		buttonBox.addView(iconView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				layoutWeightSelect));
		final EditText textInput = new EditText(context);
		textInput.setEnabled(true);
		textInput.setFocusable(true);
		textInput.setText(itemText);
		buttonBox.addView(textInput, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				layoutWeightTextInput));
		ImageButton deleteButton = new ImageButton(context);
		deleteButton.setImageResource(android.R.drawable.ic_delete);
		deleteButton.setBackgroundColor(Color.TRANSPARENT);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "Adding item " + textInput.getText().toString()
						+ " to the remove list");
				if (!itemPosToRemove.contains(itemNrInList)) {
					textInput.setPaintFlags(textInput.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
					textInput.setEnabled(false);
					textInput.invalidate();
					itemPosToRemove.add(itemNrInList);
				} else {
					itemPosToRemove.remove((Integer) itemNrInList);
					textInput.setPaintFlags(textInput.getPaintFlags()
							& (~Paint.STRIKE_THRU_TEXT_FLAG));
					textInput.setEnabled(true);
					textInput.invalidate();
				}
			}
		});
		buttonBox.addView(deleteButton, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				layoutWeightDelete));

		return buttonBox;
	}

	protected void setAllButtonsToUnChecked(LinearLayout container) {
		for (int i = 0; i < container.getChildCount(); i++) {
			ImageButton iconView = (ImageButton) ((LinearLayout) container
					.getChildAt(i)).getChildAt(0);
			iconView.setImageResource(android.R.drawable.radiobutton_off_background);
		}
	}

	@Override
	public boolean save() {

		if (selectedItemNr < 0 || selectedItemNr >= listView.getChildCount()) {
			Log.w(TAG,
					"selectedItemNr=" + selectedItemNr
							+ " not in allowed range. itemList.size()="
							+ itemList.size());
			return false;
		}
		if (itemPosToRemove.contains(selectedItemNr)) {
			Log.w(TAG, "itemPosToRemove " + itemPosToRemove
					+ " is not allowed to contain selectedItemNr="
					+ selectedItemNr);
			return false;
		}

		for (int i = 0; i < listView.getChildCount(); i++) {
			String newItemText = ((TextView) ((LinearLayout) listView
					.getChildAt(i)).getChildAt(1)).getText().toString();
			if (i <= itemList.size() - 1) {
				itemList.set(i, newItemText);
			} else {
				addNewItemToList(itemList, newItemText);
			}

		}

		Collections.sort(itemPosToRemove);
		for (int i = itemPosToRemove.size() - 1; i >= 0; i--) {
			removeItemFromList(itemList, itemPosToRemove.get(i));
		}
		itemPosToRemove.clear();

		return save(itemList, selectedItemNr);

	}

	public void removeItemFromList(List<String> l, int itemNrToRemove) {
		l.remove(itemNrToRemove);
	}

	public void addNewItemToList(List<String> l, String newItemText) {
		l.add(newItemText);
	}

	public abstract ArrayList<String> getItemList();

	/**
	 * @param itemList
	 *            the item list which is passed in the {@link Modifier} when
	 *            {@link M_RadioButtonListCreator2#getItemList()} is called.
	 *            This object is just returned here
	 * @param selectedItemNr
	 *            the list with all item ids for the items which are checked.
	 *            You can also check if the list is empty to not allow selecting
	 *            no anwers
	 * @return
	 */
	public abstract boolean save(ArrayList<String> itemList,
			Integer selectedItemNr);

}
