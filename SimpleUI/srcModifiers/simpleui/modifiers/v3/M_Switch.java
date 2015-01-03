package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

public abstract class M_Switch implements ModifierInterface {

	private SwitchCompat s;
	private final String varName;

	public M_Switch(String varName) {
		this.varName = varName;
	}

	@Override
	public View getView(Context context) {
		s = new SwitchCompat(context);
		s.setText(varName);
		s.setChecked(loadVar());
		s.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);
		return s;
	}

	public abstract boolean loadVar();

	@Override
	public boolean save() {
		return save(s.isChecked());
	}

	public abstract boolean save(boolean checked);

}
