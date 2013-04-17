package v2.simpleUi.customViews;

import java.io.File;

import v2.simpleUi.M_Container;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.util.ProgressScreen;
import v3.M_MakePhoto;
import android.R;
import android.app.Activity;
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

	protected void addPhotoModifier(Context context) {
		ModifierInterface m = new M_MakePhoto() {
			@Override
			public String getModifierCaption() {
				return "Image";
			}

			@Override
			public boolean save(Activity activity, File takenBitmapFile) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String getTextOnTakePhotoButton() {
				// TODO Auto-generated method stub
				return "Take";
			}

			@Override
			public String getTextOnLoadFileButton() {
				return "Load";
			}

			@Override
			public String getImageFileName() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		addView(m.getView(context));
	}

}
