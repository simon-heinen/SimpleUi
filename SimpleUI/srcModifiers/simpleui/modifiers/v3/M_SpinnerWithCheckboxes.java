package simpleui.modifiers.v3;

import java.util.List;

import simpleui.modifiers.ModifierInterface;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public abstract class M_SpinnerWithCheckboxes implements ModifierInterface,
		OnMultiChoiceClickListener {

	private static final String LOG_TAG = "M_SpinnerWithCheckboxes";

	public static class SpinnerItem {

		private int id;
		private String text;
		private boolean checked;

		public SpinnerItem(int id, String text, boolean checked) {
			this.id = id;
			this.text = text;
			this.checked = checked;
		}

		public String getText() {
			return text;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public boolean isChecked() {
			return checked;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return getText();
		}

	}

	private List<SpinnerItem> list;

	@Override
	public View getView(final Context context) {
		Button b = new Button(context);
		final String varName = getVarName();
		b.setText(varName);
		list = loadListToDisplay(); // TODO clone the list here?
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(varName);
				builder.setMultiChoiceItems(getItems(list),
						getCheckedItems(list), M_SpinnerWithCheckboxes.this);
				builder.setNeutralButton(loadDialogCloseButtonText(),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								onDialogClosed(dialog);
							}

						});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		return b;
	}

	public String loadDialogCloseButtonText() {
		return "Ok";
	}

	public abstract List<SpinnerItem> loadListToDisplay();

	public abstract String getVarName();

	/**
	 * Override this to react on the close event
	 * 
	 * @param dialog
	 */
	private void onDialogClosed(DialogInterface dialog) {
		dialog.dismiss();
	}

	protected boolean[] getCheckedItems(List<SpinnerItem> list) {
		boolean[] result = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i).isChecked();
		}
		return result;
	}

	protected String[] getItems(List<SpinnerItem> list) {
		String[] result = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i).getText();
		}
		return result;
	}

	@Override
	public boolean save() {
		if (list == null) {
			Log.e(LOG_TAG, "List was null!");
			return false;
		}
		return save(list);
	}

	/**
	 * called when the items have been saved
	 * 
	 * @param list
	 *            the same list object you passed in
	 *            {@link M_SpinnerWithCheckboxes#loadListToDisplay()}
	 * @return
	 */
	public abstract boolean save(List<SpinnerItem> list);

	@Override
	public void onClick(DialogInterface arg0, int posInList, boolean checked) {
		list.get(posInList).setChecked(checked);
	}
}
