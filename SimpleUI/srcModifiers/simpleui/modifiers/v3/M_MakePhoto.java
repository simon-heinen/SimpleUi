package simpleui.modifiers.v3;

import java.io.File;
import java.io.IOException;

import simpleui.modifiers.ModifierInterface;
import simpleui.util.ActivityLifecycleListener;
import simpleui.util.IO;
import simpleui.util.ImageTransform;
import simpleui.util.KeepProcessAliveService;
import android.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ipaulpro.afilechooser.utils.FileUtils;

@TargetApi(5)
public abstract class M_MakePhoto implements ModifierInterface,
		ActivityLifecycleListener {

	public final static int TAKE_PICTURE = 3672;
	public static final int SELECT_FROM_FILE = 3463;

	private int maxWidth = 640;
	private int maxHeight = 480;
	private int imageQuality = 60;

	private static final String LOG_TAG = "M_MakePhoto";

	private Bitmap takenBitmap;
	private Uri takenBitmapUri;

	private M_ImageView imageViewModifier;

	private Activity activity;

	private String imageFileName;

	/**
	 * if true the selected image from the gallery will be rewritten to the SD
	 * card
	 */
	private boolean rewriteImageToStorage = false;
	private String fileType = "*";

	public M_MakePhoto() {
	}

	/**
	 * @param fileType
	 *            on default the file type will be *
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public M_MakePhoto(Uri uri) {
		if (uri != null) {
			setFileToLoadInImageView(IO.toFile(uri));
		}
	}

	public M_MakePhoto(File f) {
		setTakenBitmapUri(f);
	}

	private void setTakenBitmapUri(File takenBitmapFile) {
		if (takenBitmapFile != null) {
			takenBitmapUri = IO.toUri(takenBitmapFile);
		}
	}

	public void setFileToLoadInImageView(File bitmap) {
		try {
			setTakenBitmapUri(bitmap);
			takenBitmap = IO.loadBitmapFromUri(takenBitmapUri);
			refreshImageInImageView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param startBitmap
	 *            the bitmap to show when the view is created
	 * @param maxWidth
	 *            e.g. 640
	 * @param maxHeight
	 *            e.g. 480
	 * @param jpgQuality
	 *            from 0 (small file size, bad quality) to 100 (large file size
	 *            goode image quality)
	 * @param rewriteImageToStorage
	 *            if true the selected image from the gallery will be rewritten
	 *            to the SD card (is false on default)
	 */
	public M_MakePhoto(Bitmap startBitmap, int maxWidth, int maxHeight,
			int jpgQuality, boolean rewriteImageToStorage) {
		this.takenBitmap = startBitmap;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.imageQuality = jpgQuality;
		this.rewriteImageToStorage = rewriteImageToStorage;
	}

	@Override
	public View getView(Context context) {

		LinearLayout box = new LinearLayout(context);
		box.setOrientation(LinearLayout.VERTICAL);

		String caption = getModifierCaption();
		if (caption != null) {
			M_Caption c = new M_Caption(caption);
			box.addView(c.getView(context));
		}

		imageViewModifier = new M_ImageView();

		if (takenBitmap != null && takenBitmap.isRecycled()) {
			Log.w(LOG_TAG, "Image bitmap was recycled but "
					+ "not null so setting it to null");
			takenBitmap = null;
		}

		if (takenBitmapUri != null && takenBitmap == null) {
			try {
				setTakenBitmap(takenBitmap = IO
						.loadBitmapFromUri(takenBitmapUri));
				resizeBitmap(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		box.addView(imageViewModifier.getView(context));
		refreshImageInImageView();

		M_Button bTakePhoto = new M_Button(R.drawable.ic_menu_camera,
				getTextOnTakePhotoButton()) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				takePhoto((Activity) context);
			}
		};
		box.addView(bTakePhoto.getView(context));

		M_Button bSelectFromFile = new M_Button(R.drawable.ic_menu_gallery,
				getTextOnLoadFileButton()) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				selectPhotoFromFile((Activity) context);
			}
		};
		box.addView(bSelectFromFile.getView(context));

		M_Button deleteButton = new M_Button(R.drawable.ic_menu_delete,
				getTextOnDeleteButton()) {

			@Override
			public void onClick(Context context, Button clickedButton) {
				if (onDeleteRequest((Activity) context)) {
					removeImage();
				}
			}
		};
		box.addView(deleteButton.getView(context));

		return box;
	}

	public void removeImage() {
		imageViewModifier.removeImage();
		takenBitmapUri = null;
	}

	public abstract boolean onDeleteRequest(Activity context);

	public abstract String getTextOnDeleteButton();

	/**
	 * @return null to not add a caption
	 */
	public abstract String getModifierCaption();

	public abstract String getTextOnLoadFileButton();

	public abstract String getTextOnTakePhotoButton();

	public void takePhoto(Activity activity) {

		/*
		 * TODO check if sd card available if yes then do it the current way if
		 * no use
		 * 
		 * i.putExtra( android.provider.MediaStore.EXTRA_OUTPUT,
		 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		 * 
		 * instead and hope that the device returns an data!=null in
		 * onActivityResult(Activity a, int requestCode, int resultCode, Intent
		 * data)
		 */

		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		imageFileName = getImageFileName();
		File file = new File(Environment.getExternalStorageDirectory(),
				imageFileName);

		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			takenBitmapUri = IO.toUri(file);
			i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, takenBitmapUri);
			KeepProcessAliveService.startKeepAliveService(activity);
			Log.d(LOG_TAG, "Starting image capture to store in file: " + file);
			activity.startActivityForResult(i, TAKE_PICTURE);
		} catch (IOException e) {
			onImageCantBeStoredInStorage(e);
		}

	}

	@SuppressLint("InlinedApi")
	protected void selectPhotoFromFile(Activity context) {
		// Intent intent = new Intent(Intent.ACTION_PICK,
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Intent intent;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
		} else {
			intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
		}
		intent.setType("image/" + fileType);
		KeepProcessAliveService.startKeepAliveService(context);
		context.startActivityForResult(intent, M_MakePhoto.SELECT_FROM_FILE);
	}

	public void onImageCantBeStoredInStorage(IOException e) {
		e.printStackTrace();
	}

	/**
	 * TODO
	 * http://stackoverflow.com/questions/2169649/open-an-image-in-androids-
	 * built-in-gallery-app-programmatically/4470069#4470069
	 * 
	 * @param a
	 * @param intent
	 */
	@SuppressLint("NewApi")
	private void loadBitmapFromFile(Activity a, Intent intent) {
		if (intent == null) {
			Log.e(LOG_TAG, "Passed intent was null");
			return;
		}
		if (intent.getData() == null) {
			Log.e(LOG_TAG, "The intent.getData()=null of intent=" + intent);
			return;
		}

		Uri selectedImageUri = intent.getData();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Log.i(LOG_TAG, "Using Kitkat version of intent.getData()");
			final int takeFlags = intent.getFlags()
					& (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			// Check for the freshest data.
			a.getContentResolver().takePersistableUriPermission(
					selectedImageUri, takeFlags);
		}

		// MEDIA GALLERY
		String filePath = null;
		try {
			filePath = getPathFromImageFileSelectionIntent(a, selectedImageUri);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (filePath == null) {
			Log.d(LOG_TAG, "Loading image from gallery intent");
			filePath = getFilePathFromGalleryIntent(a, selectedImageUri);
		}

		if (filePath == null) {
			Log.d(LOG_TAG, "Loading image from FileUtils helper");
			filePath = FileUtils.getPath(a, selectedImageUri);
		}

		if (filePath == null) {
			Log.e(LOG_TAG, "Could not load image from intent " + intent);
			return;
		}

		Log.i(LOG_TAG, "Loading bitmap from " + filePath);
		try {
			setTakenBitmapUri(new File(filePath));
			setTakenBitmap(BitmapFactory.decodeFile(filePath));
			resizeBitmap(a);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error while loading bitmap from " + filePath);
			e.printStackTrace();
		}
	}

	private void resizeBitmap(Context context) {
		if (takenBitmap != null) {
			setTakenBitmap(ImageTransform.rotateAndResizeBitmap(context,
					takenBitmap, takenBitmapUri, maxWidth, maxHeight));
			if (rewriteImageToStorage) {
				setTakenBitmapUri(new File(
						Environment.getExternalStorageDirectory(),
						getImageFileName()));
				ImageTransform.tryToStoreBitmapToTargetFile(takenBitmap,
						IO.toFile(takenBitmapUri), imageQuality);
			}
		} else {
			Log.e(LOG_TAG, "Could not load bitmap from file " + takenBitmapUri);
		}
	}

	/**
	 * The name the taken/selected file should have. If you select an existing
	 * file a copy will be created and this copy will be used
	 * 
	 * @return e.g. "/myAppCache/" + new Date().getTime() + ".jpg"
	 */
	public abstract String getImageFileName();

	@Override
	public boolean save() {
		if (takenBitmapUri == null) {
			return true;
		}
		if (save(activity, IO.toFile(takenBitmapUri))) {
			Log.i(LOG_TAG,
					"Save action correct so setting bitmap reference to null");
			setTakenBitmap(null);
			return true;
		}
		return false;
	}

	/**
	 * @param activity
	 * @param takenBitmapFile
	 * @return
	 */
	public abstract boolean save(Activity activity, File takenBitmapFile);

	@Override
	public boolean onCloseWindowRequest(Activity a) {
		return true;
	}

	@Override
	public void onStop(Activity activity) {
	}

	@Override
	public void onActivityResult(Activity a, int requestCode, int resultCode,
			Intent data) {

		Log.d(LOG_TAG, "onActivityResult");
		Log.d(LOG_TAG, "resultCode=" + resultCode);
		Log.d(LOG_TAG, "requestCode=" + requestCode);
		if (resultCode == Activity.RESULT_OK) {
			activity = a;
			if (requestCode == M_MakePhoto.TAKE_PICTURE) {
				getBitmap(a, data);
			} else if (requestCode == M_MakePhoto.SELECT_FROM_FILE) {
				loadBitmapFromFile(a, data);
			}
			refreshImageInImageView();
		}
		KeepProcessAliveService.stopKeepAliveService();
	}

	private void refreshImageInImageView() {
		if (imageViewModifier != null && takenBitmap != null) {
			// TODO process image
			Log.d(LOG_TAG, "takenBitmap.getWidth()=" + takenBitmap.getWidth());
			Log.d(LOG_TAG, "takenBitmap.getHeight()=" + takenBitmap.getHeight());
			imageViewModifier.setImage(takenBitmapUri, takenBitmap);
		} else if (imageViewModifier != null && takenBitmapUri != null) {
			Log.d(LOG_TAG, "refreshImageInImageView with takenBitmapUri="
					+ takenBitmapUri);
			imageViewModifier.setImage(takenBitmapUri, null);
		}
	}

	public static String getPathFromImageFileSelectionIntent(Activity a, Uri uri) {
		Log.i(LOG_TAG, "Loading image from storage path uri: " + uri);
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = a.managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(columnIndex);
		} else {
			return uri.getPath();
		}
	}

	@Deprecated
	private String getFilePathFromGalleryIntent(Activity a, Uri uri) {
		try {
			String[] filePathColumn = { MediaColumns.DATA };
			Cursor cursor = a.getContentResolver().query(uri, filePathColumn,
					null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private void getBitmap(Activity a, Intent data) {
		try {
			setTakenBitmap((Bitmap) data.getExtras().get("data"));
			Log.i(LOG_TAG,
					"Got small image preview for backup, now trying to load real bitmap");
		} catch (Exception e1) {
			Log.i(LOG_TAG, "Could not load the small image preview "
					+ "for backup (not possible on some devices).");
		}
		try {

			if (takenBitmapUri == null && data != null) {
				takenBitmapUri = data.getData();
			}
			a.getContentResolver().notifyChange(takenBitmapUri, null);

			setTakenBitmap(rotateAndResizeReceivedImage(a, takenBitmapUri,
					maxWidth, maxHeight));

			setTakenBitmapUri(new File(
					Environment.getExternalStorageDirectory(), imageFileName));
			ImageTransform.tryToStoreBitmapToTargetFile(takenBitmap,
					IO.toFile(takenBitmapUri), imageQuality);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTakenBitmap(Bitmap takenBitmap) {
		this.takenBitmap = takenBitmap;
	}

	@Override
	public void onPause(Activity activity) {
	}

	@Override
	public void onResume(Activity activity) {
	}

	private static Bitmap rotateAndResizeReceivedImage(Context context,
			Uri uri, int maxWidthInPixel, int maxHeightInPixel) {
		Log.d(LOG_TAG, "Loading bitmap object from uri: " + uri);
		Bitmap b = ImageTransform.getBitmapFromUri(context, uri,
				maxWidthInPixel, maxHeightInPixel);
		b = ImageTransform.rotateAndResizeBitmap(context, b, uri,
				maxWidthInPixel, maxHeightInPixel);
		return b;
	}

}
