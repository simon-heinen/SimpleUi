package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;

import com.googlecode.simpleui.library.R;

// TODO
public class M_Slider implements ModifierInterface {

	@Override
	public View getView(Context context) {
		SeekBar s = (SeekBar) View.inflate(context,
				R.layout.material_factory_seekbar, null);
		return s;
	}

	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

}
