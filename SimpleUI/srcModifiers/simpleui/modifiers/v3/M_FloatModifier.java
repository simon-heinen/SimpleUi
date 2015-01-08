package simpleui.modifiers.v3;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public abstract class M_FloatModifier extends M_TextModifier {
	private Float minimumValue;
	private Float maximumValue;

	public void setMinimumAndMaximumValue(Float minValue, Float maxValue) {
		minimumValue = minValue;
		maximumValue = maxValue;
	}

	public Float getMaximumValue() {
		return maximumValue;
	}

	public Float getMinimumValue() {
		return minimumValue;
	}

	@Override
	public void applyTextFilterIfNeeded(EditText e) {
		e.setInputType(InputType.TYPE_CLASS_NUMBER);
		e.setKeyListener(new DigitsKeyListener(true, false));
		if (minimumValue != null && maximumValue != null) {
			setMinMaxFilterFor(e, minimumValue, maximumValue);
		}
	}

	public boolean setToMinValue() {
		if (getEditText() != null && isEditable() && getMinimumValue() != null) {
			getMyHandler().post(new Runnable() {
				@Override
				public void run() {
					getEditText().setText("" + getMinimumValue());
				}
			});
			return true;
		}
		return false;
	}

	public boolean setToMaxValue() {
		if (getEditText() != null && isEditable() && getMaximumValue() != null) {
			getMyHandler().post(new Runnable() {
				@Override
				public void run() {
					getEditText().setText("" + getMaximumValue());
				}
			});
			return true;
		}
		return false;
	}

	private static void setMinMaxFilterFor(EditText e, final Float min,
			final Float max) {
		e.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!"".equals(s.toString())) {
					try {
						float v = Float.parseFloat(s.toString());
						if (v < min) {
							s.clear();
							s.append("" + min);
						} else if (v > max) {
							s.clear();
							s.append("" + max);
						}
					} catch (NumberFormatException e) {
					}
				}
			}
		});
	}

	@Override
	public boolean save(String newValue) {
		try {
			return saveFloat(Float.parseFloat(newValue));
		} catch (NumberFormatException e) {
		}
		getEditText().requestFocus();
		return false;
	}

	@Override
	public String load() {
		return "" + loadFloat();
	}

	public abstract float loadFloat();

	public abstract boolean saveFloat(float floatValue);
}
