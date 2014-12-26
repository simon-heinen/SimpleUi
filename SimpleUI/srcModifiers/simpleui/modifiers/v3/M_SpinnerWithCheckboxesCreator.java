package simpleui.modifiers.v3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simpleui.modifiers.ModifierInterface;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.googlecode.simpleui.library.R;

/**
 * use {@link M_SpinnerWithCheckboxesCreator2} instead
 * 
 * @author Spobo
 * 
 */
@Deprecated
public abstract class M_SpinnerWithCheckboxesCreator implements
		ModifierInterface {

	private static final String TAG = "M_SpinnerWithCheckboxesCreator";

	public interface SpinnerItem {
		int getId();

		void setId(int newId);

		String getText();

		void setText(String newText);

		boolean isSelected();

		void setSelected(boolean selected);

	}

	private List<SpinnerItem> list;

	private final int layoutWeightSelect = 1;
	private final int layoutWeightTextInput = 2;
	private final int layoutWeightDelete = 1;

	private List<Integer> itemsToRemove;
	private List<Integer> itemsToSelect;
	private LinearLayout listView;

	@Override
	public View getView(final Context context) {
		listView = new LinearLayout(context);
		listView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		listView.setOrientation(LinearLayout.VERTICAL);
		list = getItemList();
		itemsToRemove = new ArrayList<Integer>();
		itemsToSelect = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			final int index = i;
			final SpinnerItem item = list.get(index);
			LinearLayout button = createOptionRow(context, index, item);
			listView.addView(button, new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		}
		return listView;
	}

	private LinearLayout createOptionRow(final Context context,
			final int index, final SpinnerItem item) {
		LinearLayout buttonBox = new LinearLayout(context);
		buttonBox.setOrientation(LinearLayout.HORIZONTAL);
		final ImageButton iconView = new ImageButton(context);
		iconView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ((item != null && item.isSelected())
						|| itemsToSelect.contains(index)) {
					if (itemsToSelect.contains(index)) {
						itemsToSelect.remove((Integer) index);
					} else {
						itemsToSelect.add(index);
					}
					iconView.setImageResource(android.R.drawable.radiobutton_off_background);
				} else {
					itemsToSelect.add(index);
					iconView.setImageResource(android.R.drawable.radiobutton_on_background);
				}

			}
		});
		if (item != null && item.isSelected()) {
			iconView.setImageResource(android.R.drawable.radiobutton_on_background);
		} else {
			iconView.setImageResource(android.R.drawable.radiobutton_off_background);
		}
		iconView.setBackgroundColor(Color.TRANSPARENT);
		buttonBox.addView(iconView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				layoutWeightSelect));
		final EditText textInput = (EditText) View.inflate(context,
				R.layout.material_factory_edittext, null);
		textInput.setEnabled(true);
		textInput.setFocusable(true);
		if (item != null) {
			textInput.setText(item.getText());
		}
		buttonBox.addView(textInput, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				layoutWeightTextInput));
		ImageButton deleteButton = new ImageButton(context);
		deleteButton.setImageResource(android.R.drawable.ic_delete);
		deleteButton.setBackgroundColor(Color.TRANSPARENT);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "Trying to remove item with text: "
						+ textInput.getText().toString());
				if (textInput.getText().toString().equals("")) {
					listView.removeViewAt(index);
					if (index < list.size()) {
						list.remove(index);
					}
				} else {
					if (!itemsToRemove.contains(index)) {
						textInput.setPaintFlags(textInput.getPaintFlags()
								| Paint.STRIKE_THRU_TEXT_FLAG);
						textInput.invalidate();
						itemsToRemove.add(index);
					} else {
						itemsToRemove.remove((Integer) index);
						textInput.setPaintFlags(textInput.getPaintFlags()
								& (~Paint.STRIKE_THRU_TEXT_FLAG));
						textInput.invalidate();
					}
				}
				Log.i(TAG, "Items to remove: " + itemsToRemove);

			}
		});
		buttonBox.addView(deleteButton, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				layoutWeightDelete));

		return buttonBox;
	}

	@Override
	public boolean save() {

		for (int i = 0; i < listView.getChildCount(); i++) {
			String itemText = ((TextView) ((LinearLayout) listView
					.getChildAt(i)).getChildAt(1)).getText().toString();
			if (i <= list.size() - 1) {
				SpinnerItem itemToSave = list.get(i);
				itemToSave.setText(itemText);
				itemToSave.setId(i);
			} else {
				addNewItemToSelectableList(i, itemText);
			}

		}

		list = getItemList();

		if (itemsToSelect != null && !itemsToSelect.isEmpty()) {
			Collections.sort(itemsToSelect);
			for (int i = 0; i < itemsToSelect.size(); i++) {
				Log.e(TAG, "list=" + list);
				Log.e(TAG, "itemsToSelect=" + itemsToSelect);
				SpinnerItem itemToSelect = list.get(itemsToSelect.get(i));
				if (itemToSelect.isSelected()) {
					itemToSelect.setSelected(false);
				} else {
					itemToSelect.setSelected(true);
				}
			}
		}
		Collections.sort(itemsToRemove);
		for (int i = itemsToRemove.size() - 1; i >= 0; i--) {
			removeItem(itemsToRemove.get(i));
		}

		return true;

	}

	public void addNewEmptyItem(Context context) {
		LinearLayout emptyItem = createOptionRow(context, list.size(), null);
		listView.addView(emptyItem, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		emptyItem.requestFocus();
	}

	public abstract List<SpinnerItem> getItemList();

	public abstract void removeItem(int id);

	public abstract void addNewItemToSelectableList(int id, String text);

}
