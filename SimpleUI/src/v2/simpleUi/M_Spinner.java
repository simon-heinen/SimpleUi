package v2.simpleUi;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class M_Spinner implements ModifierInterface {

	private static final String LOG_TAG = "M_Spinner";
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public static class SpinnerItem {

		private int id;
		private String text;

		public SpinnerItem(int id, String text) {
			this.id = id;
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return getText();
		}

	}

	private Spinner s;
	private boolean editable = true;
	private Integer selectedItemPos;
	private float weightOfDescription = 1;
	private float weightOfSpinner = 1;
	private List<SpinnerItem> list;

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfInputText(float weightOfInputText) {
		this.weightOfSpinner = weightOfInputText;
	}

	@Override
	public View getView(final Context context) {

		LinearLayout container = new LinearLayout(context);
		container.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		container.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfDescription);
		LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfSpinner);

		String varName = getVarName();
		if (varName != null) {
			TextView nameText = new TextView(context);
			nameText.setText(varName);
			nameText.setLayoutParams(p);
			container.addView(nameText);
		}

		s = new Spinner(context);
		s.setLayoutParams(p2);
		s.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					reloadItemsInSpinner(context);
				}
				return false;
			}

		});
		s.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
					reloadItemsInSpinner(context);
				}
				return false;
			}
		});
		s.setPrompt(getTitleForSpinnerBox());
		setEditable(isEditable());
		reloadItemsInSpinner(context);

		container.addView(s);

		return container;
	}

	public void reloadItemsInSpinner(final Context context) {
		CheckBox x;

		list = loadListToDisplay();
		ArrayAdapter<SpinnerItem> a = new ArrayAdapter<SpinnerItem>(context,
				android.R.layout.simple_spinner_item, list);
		a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		int oldPos = s.getSelectedItemPosition();
		if (oldPos != AdapterView.INVALID_POSITION) {
			selectedItemPos = oldPos;
		}
		s.setAdapter(a);
		if (selectedItemPos == null) {
			Log.i(LOG_TAG, "selectedItemPos was null, "
					+ "using loadSelectedItemId()");
			setSelectedItemId(loadSelectedItemId());
		} else {
			selectInSpinner(selectedItemPos);
		}
	}

	/**
	 * override this to specify a custon title for the spinner box. on default
	 * the text for the spinner modifier is used
	 * 
	 * @return
	 */
	public CharSequence getTitleForSpinnerBox() {
		return getVarName();
	}

	public abstract int loadSelectedItemId();

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (s != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					s.setEnabled(isEditable());
					s.setFocusable(isEditable());
				}
			});
		}
	}

	public boolean isEditable() {
		return editable;
	}

	@Override
	public boolean save() {
		return save((SpinnerItem) s.getSelectedItem());
	}

	public abstract boolean save(SpinnerItem selectedItem);

	public abstract String getVarName();

	public abstract List<SpinnerItem> loadListToDisplay();

	public boolean setSelectedItemId(int selectedItemId) {

		if (selectedItemId < 0 || selectedItemId >= list.size()) {
			Log.w(LOG_TAG, "selectedItemId was out of range (selectedItemId="
					+ selectedItemId + ")");
			return false;
		}

		if (selectedItemId < list.size()
				&& list.get(selectedItemId).getId() == selectedItemId) {
			selectInSpinner(selectedItemId);
			return true;
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == selectedItemId) {
				selectInSpinner(i);
				return true;
			}
		}
		return false;
	}

	public void selectInSpinner(int posInList) {
		this.selectedItemPos = posInList;
		if (s != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					Log.i(LOG_TAG, "selected item pos=" + selectedItemPos);
					s.setSelection(selectedItemPos);
				}
			});
		}
	}
}
