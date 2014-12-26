package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.googlecode.simpleui.library.R;

public abstract class M_Checkbox implements ModifierInterface, UiDecoratable {

	private CheckBox checkbox;
	private UiDecorator myDecorator;
	private boolean editable = true;
	private float weightOfDescription = 1;
	private float weightOfInputText = 1;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfInputText(float weightOfInputText) {
		this.weightOfInputText = weightOfInputText;
	}

	@Override
	public View getView(final Context context) {
		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER_VERTICAL);
		l.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		TextView t = new TextView(context);
		t.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfDescription));
		t.setText(this.getVarName());
		l.addView(t);

		t.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkbox != null && checkbox.isEnabled()) {
					checkbox.setChecked(!checkbox.isChecked());
				}
			}
		});

		checkbox = (CheckBox) View.inflate(context,
				R.layout.material_factory_checkbox, null);

		checkbox.setChecked(loadVar());
		checkbox.setEnabled(editable);
		checkbox.setFocusable(editable);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!M_Checkbox.this.onCheckedChanged(context, checkbox,
						isChecked)) {
					checkbox.setChecked(!isChecked);
				}
			}

		});

		LinearLayout l2 = new LinearLayout(context);
		l2.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfInputText));
		l2.setGravity(Gravity.RIGHT);
		l2.addView(checkbox);
		l.addView(l2);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, t, level, UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, checkbox, level,
					UiDecorator.TYPE_EDIT_TEXT);
		}

		if (checkboxCreatedListener != null) {
			checkboxCreatedListener.onUiCreated(checkbox);
		}
		if (textCreatedListener != null) {
			textCreatedListener.onUiCreated(t);
		}

		return l;
	}

	private UiCreateListener<CheckBox> checkboxCreatedListener;
	private UiCreateListener<TextView> textCreatedListener;

	public void setCheckboxCreatedListener(
			UiCreateListener<CheckBox> checkboxCreatedListener) {
		this.checkboxCreatedListener = checkboxCreatedListener;
	}

	public void setTextCreatedListener(
			UiCreateListener<TextView> textCreatedListener) {
		this.textCreatedListener = textCreatedListener;
	}

	/**
	 * @param context
	 * @param e
	 * @param isChecked
	 * @return true if its allowed to change the value
	 */
	public boolean onCheckedChanged(Context context, CheckBox e,
			boolean isChecked) {
		// on default do not react to this
		return true;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (checkbox != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					checkbox.setEnabled(isEditable());
				}
			});
		}
	}

	public void setBoolValueOfViewIfPossible(final boolean newValue) {
		if (checkbox != null) {
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					checkbox.setChecked(newValue);
				}
			});
		}
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	public abstract boolean loadVar();

	public abstract CharSequence getVarName();

	public abstract boolean save(boolean newValue);

	@Override
	public boolean save() {
		if (!editable) {
			return true;
		}
		return save(checkbox.isChecked());
	}

}