package v3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import v2.simpleUi.ModifierInterface;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * use {@link M_RadioButtonListCreator2} instead
 */
@Deprecated
public abstract class M_RadioButtonListCreator implements ModifierInterface {

	private static final String TAG = "M_RadioButtonListCreator";

	public interface SelectableItem {
		int getId();

		void setId(int newId);

		String getText();

		void setText(String newText);
	}

	private RadioGroup group;
	private List<SelectableItem> list;
	private int selectedItem = -1;

	private final int layoutWeightSelect = 1;
	private final int layoutWeightTextInput = 2;
	private final int layoutWeightDelete = 1;

	private List<Integer> itemsToRemove;

	@Override
	public View getView(final Context context) {
		group = new RadioGroup(context);
		list = getItemList();
		selectedItem = getSelectedItemId();
		if (selectedItem >= 0) {
			group.check(selectedItem);
		}
		itemsToRemove = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			final int index = i;
			final SelectableItem item = list.get(index);
			LinearLayout button = createOptionRow(context, index, item);
			group.addView(button, new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		}
		group.setEnabled(true);
		group.setFocusable(true);
		return group;
	}

	private LinearLayout createOptionRow(final Context context,
			final int index, final SelectableItem item) {
		LinearLayout buttonBox = new LinearLayout(context);
		buttonBox.setOrientation(LinearLayout.HORIZONTAL);
		final ImageButton iconView = new ImageButton(context);
		iconView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (group.getCheckedRadioButtonId() >= 0) {
					ImageButton buttonToUncheck = (ImageButton) (((LinearLayout) group
							.getChildAt(group.getCheckedRadioButtonId()))
							.getChildAt(0));
					buttonToUncheck
							.setImageResource(android.R.drawable.radiobutton_off_background);
				}
				group.check(index);
				iconView.setImageResource(android.R.drawable.radiobutton_on_background);

			}
		});
		if (group.getCheckedRadioButtonId() == index) {
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
		if (item != null) {
			textInput.setText(item.getText());
		} else {
			textInput.setText(" ");
			textInput.setText("");
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
				if (textInput.getText().toString().equals("")) {
					group.removeViewAt(index);
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

			}
		});
		buttonBox.addView(deleteButton, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				layoutWeightDelete));

		return buttonBox;
	}

	@Override
	public boolean save() {
		for (int i = 0; i < group.getChildCount(); i++) {
			String itemText = ((TextView) ((LinearLayout) group.getChildAt(i))
					.getChildAt(1)).getText().toString();
			if (i <= list.size() - 1) {
				SelectableItem itemToSave = list.get(i);
				itemToSave.setText(itemText);
				itemToSave.setId(i);
			} else {
				addNewItemToSelectableList(i, itemText);
			}

		}

		selectedItem = group.getCheckedRadioButtonId();
		Collections.sort(itemsToRemove);
		for (int i = itemsToRemove.size() - 1; i >= 0; i--) {
			if (itemsToRemove.get(i) == selectedItem) {
				((LinearLayout) group.getChildAt(itemsToRemove.get(i)))
						.requestFocus();
				return false;
			}
			removeItem(itemsToRemove.get(i));
			if (selectedItem > itemsToRemove.get(i)) {
				selectedItem--;
			}
		}

		setSelectedItemId(selectedItem);

		return true;

	}

	public void addNewEmptyItem(Context context) {
		LinearLayout emptyItem = createOptionRow(context,
				group.getChildCount(), null);
		group.addView(emptyItem, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		emptyItem.requestFocus();
	}

	public abstract List<SelectableItem> getItemList();

	public abstract int getSelectedItemId();

	public abstract void setSelectedItemId(int newId);

	public abstract void removeItem(int id);

	public abstract void addNewItemToSelectableList(int id, String text);

}
