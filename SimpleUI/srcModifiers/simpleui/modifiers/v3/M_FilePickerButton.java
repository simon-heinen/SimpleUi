package simpleui.modifiers.v3;

import java.io.File;

import simpleui.util.ActivityLifecycleListener;
import simpleui.util.KeepProcessAliveService;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

public abstract class M_FilePickerButton extends M_Button implements
		ActivityLifecycleListener {

	private static final int SELECT_FILE_CODE = 213;

	public M_FilePickerButton(String buttonText) {
		super(R.drawable.ic_input_add, buttonText);
	}

	private File folderLocation;

	/**
	 * Allows to limit the picking process to a specific folder
	 * 
	 * @param buttonText
	 * @param folderLocation
	 */
	public M_FilePickerButton(final String buttonText, final File folderLocation) {
		this(buttonText);
		this.folderLocation = folderLocation;
	}

	@Override
	public void onClick(Context context, Button clickedButton) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		final String type = "file/*";
		if (folderLocation != null) {// User specified a folder to start from
			intent.setDataAndType(Uri.fromFile(folderLocation), type);
		} else {
			intent.setType(type);
		}
		KeepProcessAliveService.startKeepAliveService(context);
		((Activity) context).startActivityForResult(intent, SELECT_FILE_CODE);
	}

	@Override
	public void onActivityResult(Activity a, int requestCode, int resultCode,
			Intent data) {
		if (requestCode == SELECT_FILE_CODE) {
			KeepProcessAliveService.stopKeepAliveService();
			if (resultCode == Activity.RESULT_OK) {

				String filePath = M_MakePhoto
						.getPathFromImageFileSelectionIntent(a, data.getData());
				File file = new File(filePath);
				if (file.isFile()) {
					onFilePathReceived(a, filePath, file, data);
					return;
				}
			}
			onNoFileSuccessfullySelected(a, data);
		}
	}

	public void onNoFileSuccessfullySelected(Activity a, Intent data) {
		// on default do nothing
	}

	/**
	 * The class where this modifier is created in has to impelemt
	 * {@link ActivityLifecycleListener} and inform this
	 * {@link M_FilePickerButton} when
	 * {@link ActivityLifecycleListener#onActivityResult(Activity, int, int, Intent)}
	 * is called, so it has to pass the event to it
	 * 
	 * @param a
	 * @param filePath
	 * @param file
	 * @param data
	 */
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
