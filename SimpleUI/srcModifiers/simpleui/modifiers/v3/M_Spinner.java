package simpleui.modifiers.v3;

import java.util.List;

import simpleui.modifiers.ModifierInterface;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.simpleui.library.R;

public abstract class M_Spinner implements ModifierInterface {

	private static final String LOG_TAG = "M_Spinner";
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public static class SpinnerItem {

		private final int id;
		private final String text;

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

	private Spinner spinner;
	private boolean editable = true;
	private Integer selectedItemPos;
	private float weightOfDescription = 1;
	private float weightOfSpinner = 1;
	private List<SpinnerItem> spinnerItemsList;

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfInputText(float weightOfInputText) {
		this.weightOfSpinner = weightOfInputText;
	}

	@Override
	public View getView(final Context context) {
		selectedItemPos = null;
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

		spinner = (Spinner) View.inflate(context,
				R.layout.material_factory_spinner, null);
		spinner.setLayoutParams(p2);
		spinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					reloadItemsInSpinner(context);
				}
				return false;
			}

		});
		spinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
					reloadItemsInSpinner(context);
				}
				return false;
			}
		});
		spinner.setPrompt(getTitleForSpinnerBox());
		setEditable(isEditable());
		reloadItemsInSpinner(context);

		container.addView(spinner);

		return container;
	}

	public void reloadItemsInSpinner(final Context context) {
		spinnerItemsList = loadListToDisplay();
		ArrayAdapter<SpinnerItem> a = new ArrayAdapter<SpinnerItem>(context,
				android.R.layout.simple_spinner_item, spinnerItemsList) {

			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				/*
				 * There is a bug in later android versions which prevents the
				 * full text to be displayed normally when a long text is shown.
				 * this is a fix for that problem
				 */
				M_InfoText t = new M_InfoText(getItem(position).text);
				return t.getView(getContext());
			}

		};
		a.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
		int oldPos = spinner.getSelectedItemPosition();
		if (oldPos != AdapterView.INVALID_POSITION) {
			selectedItemPos = oldPos;
		}
		spinner.setAdapter(a);
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
		if (spinner != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					spinner.setEnabled(isEditable());
					spinner.setFocusable(isEditable());
				}
			});
		}
	}

	public boolean isEditable() {
		return editable;
	}

	@Override
	public boolean save() {
		if (spinner.getSelectedItemId() == AdapterView.INVALID_ROW_ID) {
			return false;
		}
		return save((SpinnerItem) spinner.getSelectedItem());
	}

	public abstract boolean save(SpinnerItem selectedItem);

	public abstract String getVarName();

	public abstract List<SpinnerItem> loadListToDisplay();

	public boolean setSelectedItemId(int selectedItemId) {
		List<SpinnerItem> list = spinnerItemsList;
		if (spinnerItemsList == null || spinnerItemsList.isEmpty()) {
			Log.e(LOG_TAG, "spinnerItemsList was null or "
					+ "empty cant select an item in it");
			return false;
		}

		if (selectedItemId < list.size()
				&& list.get(selectedItemId).getId() == selectedItemId) {
			return selectInSpinner(selectedItemId);
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == selectedItemId) {
				return selectInSpinner(i);
			}
		}
		return false;
	}

	public boolean selectInSpinner(final int posInList) {
		if (spinner == null || spinnerItemsList == null
				|| spinnerItemsList.isEmpty()) {
			Log.e(LOG_TAG, "spinner or spinnerItemsList was null or "
					+ "empty cant select an item in it");
			return false;
		}
		if (posInList < 0 || posInList >= spinnerItemsList.size()) {
			Log.e(LOG_TAG, "posInList was out of range (posInList=" + posInList
					+ ")");
			return false;
		}
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				selectedItemPos = posInList;
				Log.i(LOG_TAG, "selected item pos=" + selectedItemPos);
				spinner.setSelection(selectedItemPos);
			}
		});
		return true;
	}
}
