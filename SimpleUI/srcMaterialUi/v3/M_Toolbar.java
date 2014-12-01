package v3;

import v2.simpleUi.ModifierInterface;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.googlecode.simpleui.library.R;

public class M_Toolbar implements ModifierInterface {

	private final String title;

	public M_Toolbar(String title) {
		this.title = title;
	}

	@Override
	public View getView(Context context) {
		Toolbar t = new Toolbar(context);
		t.setTitle(title);
		t.setBackgroundColor(context.getResources().getColor(
				R.color.colorPrimaryDark));
		if (context instanceof ActionBarActivity) {
			((ActionBarActivity) context).setSupportActionBar(t);
		}
		return t;
	}

	@Override
	public boolean save() {
		return true;
	}

}
