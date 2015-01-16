package simpleui.customViews;

import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.util.ProgressScreen;
import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SimpleUiTestbed extends LinearLayout {

	public SimpleUiTestbed(Context context, AttributeSet attrs) {
		super(context, attrs);
		// addPhotoModifier(context);
		addTestContainer(context);
		addView(new ProgressScreen() {

			@Override
			public boolean onAbortRequest() {
				return true;
			}
		}.getView(context));

	}

	protected void addTestContainer(Context context) {
		M_Container c = new M_Container();
		c.add(new M_InfoText(R.drawable.ic_dialog_alert, "sdfsefswegf"));
		c.add(new M_InfoText("sdfsefswegf"));
		c.add(new M_InfoText(R.drawable.ic_dialog_alert,
				"sdf\nse\nfsw\negf\negf\negf\negf\negf"));
		addView(c.getView(context));
	}

}
