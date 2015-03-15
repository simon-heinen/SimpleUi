package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.util.Log;
import simpleui.util.MenuItemList;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.googlecode.simpleui.library.R;

/**
 * Use {@link MenuItemList} to add elements to this Toolbar
 *
 */
public class M_Toolbar implements ModifierInterface {

	private static final String LOG_TAG = M_Toolbar.class.getSimpleName();
	private final String title;

	public M_Toolbar(String title) {
		this.title = title;
	}

	@Override
	public View getView(Context context) {
		Toolbar t = new Toolbar(context);
		t.setTitle(title);
		t.setBackgroundColor(context.getResources().getColor(
				R.color.color_primary_dark));
		if (context instanceof ActionBarActivity) {
			Log.i(LOG_TAG,
					"Context was an ActionBarActivity, so registering as action bar");
			((ActionBarActivity) context).setSupportActionBar(t);
		}
		return t;
	}

	@Override
	public boolean save() {
		return true;
	}

	@Override
	public String toString() {
		if (title != null) {
			return "Title=" + title;
		}
		return super.toString();
	}
}
