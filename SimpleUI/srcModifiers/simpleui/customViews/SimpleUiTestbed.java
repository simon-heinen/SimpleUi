package simpleui.customViews;

import java.io.File;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_MakePhoto;
import simpleui.util.ProgressScreen;
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
			public String getTextOnDeleteButton() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean onDeleteRequest(Activity context) {
				// TODO Auto-generated method stub
				return true;
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
