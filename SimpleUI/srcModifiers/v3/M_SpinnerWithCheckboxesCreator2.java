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

public abstract class M_SpinnerWithCheckboxesCreator2 implements
		ModifierInterface {

	private static final String TAG = "M_SpinnerWithCheckboxesCreator";

	public interface SpinnerItem {

		String getText();

		void setText(String newText);

		boolean isSelected();

		void setSelected(boolean selected);

	}

	public static class DefaultSpinnerItem implements SpinnerItem {

		private String text;
		private boolean isChecked;

		public DefaultSpinnerItem(String text, boolean isChecked) {
			this.text = text;
			this.isChecked = isChecked;
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public void setText(String newText) {
			text = newText;
		}

		@Override
		public boolean isSelected() {
			return isChecked;
		}

		@Override
		public void setSelected(boolean selected) {
			isChecked = selected;
		}

	}

	private List<SpinnerItem> itemList;

	private final int layoutWeightSelect = 1;
	private final int layoutWeightTextInput = 2;
	private final int layoutWeightDelete = 1;

	/**
	 * The positions in the item list for all objects that have to be removed
	 */
	private List<Integer> itemPosToRemove;
	private ArrayList<Integer> selectedItemIdsList;
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
		itemPosToRemove = new ArrayList<Integer>();
		selectedItemIdsList = new ArrayList<Integer>();
		for (int i = 0; i < itemList.size(); i++) {
			if (itemList.get(i).isSelected()) {
				selectedItemIdsList.add(i);
			}
		}
		for (int i = 0; i < itemList.size(); i++) {
			final SpinnerItem item = itemList.get(i);
			LinearLayout row = createOptionRow(context, i, item.getText());
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
				if (selectedItemIdsList.contains(itemNrInList)) {
					selectedItemIdsList.remove((Integer) itemNrInList);
					iconView.setImageResource(android.R.drawable.checkbox_off_background);
				} else {
					selectedItemIdsList.add(itemNrInList);
					iconView.setImageResource(android.R.drawable.checkbox_on_background);
				}
			}
		});
		if (selectedItemIdsList.contains(itemNrInList)) {
			iconView.setImageResource(android.R.drawable.checkbox_on_background);
		} else {
			iconView.setImageResource(android.R.drawable.checkbox_off_background);
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

	@Override
	public boolean save() {

		Collections.sort(selectedItemIdsList);
		// for (int i = 0; i < itemList.size(); i++) {
		// SpinnerItem itemToSelect = itemList.get(i);
		// if (selectedItemIdsList.contains(i)) {
		// itemToSelect.setSelected(true);
		// } else {
		// itemToSelect.setSelected(false);
		// }
		// }

		for (int i = 0; i < listView.getChildCount(); i++) {
			String newItemText = ((TextView) ((LinearLayout) listView
					.getChildAt(i)).getChildAt(1)).getText().toString();
			boolean isChecked = selectedItemIdsList.contains(i);
			if (i <= itemList.size() - 1) {
				SpinnerItem itemToSave = itemList.get(i);
				itemToSave.setText(newItemText);
				itemToSave.setSelected(isChecked);
			} else {
				addNewItemToList(itemList, newItemText, isChecked);
			}

		}

		Collections.sort(itemPosToRemove);
		for (int i = itemPosToRemove.size() - 1; i >= 0; i--) {
			removeItemFromList(itemList, itemPosToRemove.get(i));
		}
		itemPosToRemove.clear();

		return save(itemList, selectedItemIdsList);

	}

	public void removeItemFromList(List<SpinnerItem> l, int itemNrToRemove) {
		l.remove(itemNrToRemove);
	}

	public void addNewItemToList(List<SpinnerItem> l, String newItemText,
			boolean itemIsChecked) {
		l.add(new DefaultSpinnerItem(newItemText, itemIsChecked));
	}

	public abstract List<SpinnerItem> getItemList();

	/**
	 * @param itemList
	 *            the item list which is passed in the {@link Modifier} when
	 *            {@link M_SpinnerWithCheckboxesCreator2#getItemList()} is
	 *            called. This object is just returned here
	 * @param selectedItemIdsList
	 *            the list with all item ids for the items which are checked.
	 *            You can also check if the list is empty to not allow selecting
	 *            no anwers
	 * @return
	 */
	public abstract boolean save(List<SpinnerItem> itemList,
			ArrayList<Integer> selectedItemIdsList);

}
