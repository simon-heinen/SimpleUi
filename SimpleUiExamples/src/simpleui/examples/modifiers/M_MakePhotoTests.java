package simpleui.examples.modifiers;

import java.io.File;

import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_MakePhoto;
import simpleui.util.ActivityLifecycleListener;
import android.app.Activity;
import android.content.Intent;

public class M_MakePhotoTests extends M_Container implements
		ActivityLifecycleListener {
	private M_MakePhoto photo;
	private File file;

	public M_MakePhotoTests() {

		photo = new M_MakePhoto(file) {

			@Override
			public boolean save(Activity activity, File takenBitmapFile) {
				file = takenBitmapFile;
				return false;
			}

			@Override
			public String getTextOnTakePhotoButton() {
				return "Make photo";
			}

			@Override
			public String getTextOnLoadFileButton() {
				return "load photo";
			}

			@Override
			public String getTextOnDeleteButton() {
				return "Delete photo";
			}

			@Override
			public boolean onDeleteRequest(Activity context) {
				file = null;
				return true;
			}

			@Override
			public String getImageFileName() {
				return "test.png";
			}

			@Override
			public String getModifierCaption() {
				return "Photo:";
			}
		};
		add(photo);
	}

	@Override
	public void onActivityResult(Activity a, int requestCode, int resultCode,
			Intent data) {
		photo.onActivityResult(a, requestCode, resultCode, data);
	}

	@Override
	public void onStop(Activity activity) {
	}

	@Override
	public void onPause(Activity activity) {
	}

	@Override
	public void onResume(Activity activity) {
	}

	@Override
	public boolean onCloseWindowRequest(Activity activity) {
		return true;
	}
}
