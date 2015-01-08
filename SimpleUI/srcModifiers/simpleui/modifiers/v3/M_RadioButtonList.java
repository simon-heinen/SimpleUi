package simpleui.modifiers.v3;

import java.util.List;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_RadioButtonList.SelectableItem;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.googlecode.simpleui.library.R;

public abstract class M_RadioButtonList<T extends SelectableItem> implements
		ModifierInterface {

	public interface SelectableItem {
		/**
		 * @return should be a positive number which is unique in its list of
		 *         {@link SelectableItem}s
		 */
		int getId();

		String getText();

	}

	public static class DefaultSelectableItem implements SelectableItem {

		private final String text;
		private final int id;

		public DefaultSelectableItem(final int id, final String text) {
			this.id = id;
			this.text = text;
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public int getId() {
			return id;
		}
	}

	private RadioGroup group;
	private boolean editable = true;
	private final Handler myHandler = new Handler(Looper.getMainLooper());

	public M_RadioButtonList() {
	}

	@Override
	public View getView(final Context context) {
		group = new RadioGroup(context);
		List<T> list = getItemList();
		for (int i = 0; i < list.size(); i++) {
			final T item = list.get(i);
			RadioButton b = (RadioButton) View.inflate(context,
					R.layout.material_factory_radiobutton, null);
			b.setId(item.getId());
			Integer selectedItemId = loadSelectedItemId();
			if (selectedItemId != null) {
				b.setChecked(selectedItemId.equals(item.getId()));
			}
			b.setText(item.getText());
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onItemSelectedByUser(context, item);
				}
			});
			group.addView(b);
		}
		setEditable(editable);
		return group;
	}

	/**
	 * @return the id of the item which should be selected or NULL if no item
	 *         should be selected
	 */
	public abstract Integer loadSelectedItemId();

	@Override
	public boolean save() {
		for (T i : getItemList()) {
			if (i.getId() == group.getCheckedRadioButtonId()) {
				return save(i);
			}
		}
		return false;
	}

	public abstract boolean save(T item);

	/**
	 * This is called as soon as the user selects an {@link SelectableItem} in
	 * the list. Normally this method does not have to do anything and the save
	 * action should only happen in the
	 * {@link M_RadioButtonList#save(SelectableItem)} method!
	 * 
	 * @param context
	 * 
	 * @param item
	 */
	public void onItemSelectedByUser(Context context, T item) {
	}

	public abstract List<T> getItemList();

	public void setEditable(final boolean editable) {
		this.editable = editable;
		if (group != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					group.setEnabled(editable);
					group.setFocusable(editable);
				}
			});

		}
	}
}
