package v3;

import java.io.File;

import v2.simpleUi.ActivityLifecycleListener;
import v2.simpleUi.M_Button;
import v2.simpleUi.util.KeepProcessAliveService;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;

public abstract class M_FilePickerButton extends M_Button implements
		ActivityLifecycleListener {

	private static final int SELECT_FILE_CODE = 213;

	public M_FilePickerButton(String buttonText) {
		super(R.drawable.ic_input_add, buttonText);
	}

	@Override
	public void onClick(Context context, Button clickedButton) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		KeepProcessAliveService.startKeepAliveService(context);
		((Activity) context).startActivityForResult(intent, SELECT_FILE_CODE);
	}

	@Override
	public void onActivityResult(Activity a, int requestCode, int resultCode,
			Intent data) {
		KeepProcessAliveService.stopKeepAliveService();
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_FILE_CODE) {
				String filePath = M_MakePhoto
						.getPathFromImageFileSelectionIntent(a, data.getData());
				File file = new File(filePath);
				if (file.isFile()) {
					onFilePathReceived(a, filePath, file, data);
					return;
				}
			}
		}
		onNoFileSuccessfullySelected(a, data);
	}

	public void onNoFileSuccessfullySelected(Activity a, Intent data) {
		// on default do nothing
	}

	public abstract void onFilePathReceived(Activity a, String filePath,
			File file, Intent data);

	@Override
	public void onStop(Activity activity) {
	}

	@Override
	public boolean onCloseWindowRequest(Activity activity) {
		return true;
	}

	@Override
	public void onPause(Activity activity) {
	}

	@Override
	public void onResume(Activity activity) {
	}

}
