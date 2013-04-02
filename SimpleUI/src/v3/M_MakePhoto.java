package v3;

import java.io.File;
import java.io.IOException;

import v2.simpleUi.ActivityLifecycleListener;
import v2.simpleUi.M_Button;
import v2.simpleUi.M_Caption;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.util.IO;
import v2.simpleUi.util.ImageTransform;
import v2.simpleUi.util.KeepProcessAliveService;
import android.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

@TargetApi(5)
public abstract class M_MakePhoto implements ModifierInterface,
		ActivityLifecycleListener {

	public final static int TAKE_PICTURE = 367289;
	public static final int SELECT_FROM_FILE = 3463270;

	private int maxWidth = 640;
	private int maxHeight = 480;
	private int imageQuality = 60;

	private static final String LOG_TAG = "M_MakePhoto";

	private Uri imageUri;
	private M_ImageView imageView;
	private Bitmap takenBitmap;

	private Activity activity;

	private String imageFileName;

	/**
	 * if true the selected image from the gallery will be rewritten to the SD
	 * card
	 */
	private boolean rewriteImageToStorage = true;

	public M_MakePhoto() {
	}

	public M_MakePhoto(Uri uri) {
		if (uri != null) {
			setFileToLoadInImageViewFromUiThread(activity, toFile(uri));
		}
	}

	public M_MakePhoto(File f) {
		setTakenBitmapFileAndUri(f);

	}

	public void setTakenBitmapFileAndUri(File takenBitmapFile) {
		if (takenBitmapFile != null) {
			imageUri = Uri.fromFile(takenBitmapFile);
		}
	}

	public void setFileToLoadInImageViewFromUiThread(Context context,
			File bitmap) {
		try {
			setTakenBitmapFileAndUri(bitmap);
			takenBitmap = IO.loadBitmapFromUri(imageUri);
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
	 * @param restoreImageFromStorage
	 *            if true the selected image from the gallery will be rewritten
	 *            to the SD card
	 */
	public M_MakePhoto(Bitmap startBitmap, int maxWidth, int maxHeight,
			int jpgQuality, boolean restoreImageFromStorage) {
		this.takenBitmap = startBitmap;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.imageQuality = jpgQuality;
		this.rewriteImageToStorage = restoreImageFromStorage;
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

		imageView = new M_ImageView();

		if (takenBitmap != null && takenBitmap.isRecycled()) {
			Log.w(LOG_TAG, "Image bitmap was recycled but "
					+ "not null so setting it to null");
			takenBitmap = null;
		}

		if (imageUri != null && takenBitmap == null) {
			try {
				takenBitmap = IO.loadBitmapFromUri(imageUri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		box.addView(imageView.getView(context));
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

		return box;
	}

	/**
	 * @return null to not add a caption
	 */
	public abstract String getModifierCaption();

	public abstract String getTextOnLoadFileButton();

	public abstract String getTextOnTakePhotoButton();

	public void takePhoto(Activity activity) {

		/*
		 * TODO check if sd card available if yes then do is the current way if
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
			imageUri = Uri.fromFile(file);
			i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
			KeepProcessAliveService.startKeepAliveService(activity);
			Log.d(LOG_TAG, "Starting image capture to store in file: " + file);
			activity.startActivityForResult(i, TAKE_PICTURE);
		} catch (IOException e) {
			onImageCantBeStoredInStorage(e);
		}

	}

	protected void selectPhotoFromFile(Activity context) {
		// Intent intent = new Intent(Intent.ACTION_PICK,
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		KeepProcessAliveService.startKeepAliveService(context);
		context.startActivityForResult(intent, M_MakePhoto.SELECT_FROM_FILE);
	}

	public void onImageCantBeStoredInStorage(IOException e) {
		e.printStackTrace();
	}

	/**
	 * @return e.g. "/myAppCache/" + new Date().getTime() + ".jpg"
	 */
	public abstract String getImageFileName();

	@Override
	public boolean save() {
		if (imageUri == null) {
			return true;
		}
		if (save(activity, takenBitmap, toFile(imageUri))) {
			Log.i(LOG_TAG,
					"Save action correct so setting bitmap reference to null");
			setTakenBitmap(null);
			return true;
		}
		return false;
	}

	private File toFile(Uri uri) {
		if (uri == null) {
			return null;
		}
		return new File(uri.getPath());
	}

	/**
	 * @param activity
	 * @param takenBitmap
	 *            if you do not need the bitmap anymore call
	 *            {@link Bitmap#recycle()} !
	 * @param takenBitmapFile
	 * @return
	 */
	public abstract boolean save(Activity activity, Bitmap takenBitmap,
			File takenBitmapFile);

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
		if (imageView != null && takenBitmap != null) {
			// TODO process image
			Log.d(LOG_TAG, "takenBitmap.getWidth()=" + takenBitmap.getWidth());
			Log.d(LOG_TAG, "takenBitmap.getHeight()=" + takenBitmap.getHeight());
			imageView.setImageBitmap(takenBitmap);
		}
	}

	/**
	 * TODO
	 * http://stackoverflow.com/questions/2169649/open-an-image-in-androids-
	 * built-in-gallery-app-programmatically/4470069#4470069
	 * 
	 * @param a
	 * @param data
	 */
	private void loadBitmapFromFile(Activity a, Intent data) {
		if (data == null || data.getData() == null) {
			Log.e(LOG_TAG, "Could not load image from intent " + data);
			return;
		}

		Uri selectedImageUri = data.getData();

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
			Log.e(LOG_TAG, "Could not load image from intent " + data);
			return;
		}

		Log.i(LOG_TAG, "Loadin bitmap from " + filePath);
		try {
			setTakenBitmapFileAndUri(new File(filePath));
			setTakenBitmap(BitmapFactory.decodeFile(filePath));
			if (takenBitmap != null) {
				setTakenBitmap(ImageTransform.rotateAndResizeBitmap(a,
						takenBitmap, imageUri, maxWidth, maxHeight));
				if (rewriteImageToStorage) {
					setTakenBitmapFileAndUri(new File(
							Environment.getExternalStorageDirectory(),
							getImageFileName()));
					ImageTransform.tryToStoreBitmapToTargetFile(takenBitmap,
							toFile(imageUri), imageQuality);
				}
			} else {
				Log.e(LOG_TAG, "Could not load bitmap from file " + filePath);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error while loading bitmap from " + filePath);
			e.printStackTrace();
		}
	}

	public static String getPathFromImageFileSelectionIntent(Activity a, Uri uri) {
		Log.i(LOG_TAG, "Loading image from storage path uri: " + uri);
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = a.managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(columnIndex);
		} else {
			return uri.getPath();
		}
	}

	@Deprecated
	private String getFilePathFromGalleryIntent(Activity a, Uri uri) {
		try {
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
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

			if (imageUri == null && data != null) {
				imageUri = data.getData();
			}
			a.getContentResolver().notifyChange(imageUri, null);

			setTakenBitmap(rotateAndResizeReceivedImage(a, imageUri, maxWidth,
					maxHeight));

			setTakenBitmapFileAndUri(new File(
					Environment.getExternalStorageDirectory(), imageFileName));
			ImageTransform.tryToStoreBitmapToTargetFile(takenBitmap,
					toFile(imageUri), imageQuality);

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
