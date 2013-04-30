package v3;

import java.io.File;

import v2.simpleUi.ModifierInterface;
import v2.simpleUi.util.IO;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class M_ImageView implements ModifierInterface {

	protected static final String LOG_TAG = "M_ImageView";
	private ImageView mImageView;

	private Uri bitmapUri;
	private Bitmap bitmap;

	private Integer imageBorderColor = Color.parseColor("#F5F1DE");
	private int imageBorderSizeInPixel = 10;
	private Bitmap oldBitmapToBeRecycled;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public M_ImageView() {
	}

	public M_ImageView(Uri uri) {
		setBitmapUri(uri);
	}

	public M_ImageView(File imageFile) {
		setBitmapUri(IO.toUri(imageFile));
	}

	private void setBitmap(Bitmap b) {
		if (bitmap != null && !bitmap.isRecycled()) {
			oldBitmapToBeRecycled = bitmap;
		}
		bitmap = b;
	}

	public void setBitmapUri(Uri bitmapUri) {
		this.bitmapUri = bitmapUri;
	}

	@Override
	public View getView(Context context) {
		mImageView = new ImageView(context) {

			@Override
			protected void onAttachedToWindow() {
				refreshImageViewFromUiThread();
				super.onAttachedToWindow();
			}

			@Override
			protected void onDetachedFromWindow() {
				super.onDetachedFromWindow();
				setBitmap(null);
			}
		};

		if (mImageView.getBackground() != null) {
			imageBorderColor = null;
		}

		LinearLayout linlay = new LinearLayout(context);
		linlay.setGravity(Gravity.CENTER_HORIZONTAL);
		int p = DEFAULT_PADDING;
		linlay.setPadding(p, p, p, p);
		linlay.addView(mImageView);
		return linlay;
	}

	private void refreshImageView() {
		if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
			// On UI thread.
			refreshImageViewFromUiThread();
		} else {
			// Not on UI thread.
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					refreshImageViewFromUiThread();
				}

			});
		}
	}

	private void refreshImageViewFromUiThread() {
		if (bitmapUri != null && (bitmap == null || bitmap.isRecycled())) {
			Log.i(LOG_TAG, "Loading new bitmap for " + bitmapUri);
			setBitmap(IO.loadBitmapFromUri(bitmapUri));
		}
		if (mImageView != null) {
			if (bitmap != null) {
				if (imageBorderColor != null) {
					mImageView.setBackgroundColor(imageBorderColor);
				}
				mImageView.setPadding(imageBorderSizeInPixel,
						imageBorderSizeInPixel, imageBorderSizeInPixel,
						imageBorderSizeInPixel);
				mImageView.setImageBitmap(bitmap);
			} else {
				if (imageBorderColor != null) {
					// else clear image border
					mImageView.setBackgroundColor(Color.TRANSPARENT);
				}
				mImageView.setImageBitmap(null);
			}
			if (oldBitmapToBeRecycled != null) {
				if (!oldBitmapToBeRecycled.isRecycled()) {
					oldBitmapToBeRecycled.recycle();
					Log.i(LOG_TAG, "Last bitmap was recycled");
				}
				oldBitmapToBeRecycled = null;
			}
		}
	}

	@Override
	public boolean save() {
		return true;
	}

	/**
	 * @param bitmapUri
	 * @param bitmap
	 *            optional. the bitmap if it was already loaded from the image
	 *            uri. reloading the bitmap from the imageUri must result in the
	 *            same bitmap as this passed one!
	 */
	public void setImage(Uri bitmapUri, Bitmap bitmap) {
		setBitmapUri(bitmapUri);
		setBitmap(bitmap);
		refreshImageView();
	}

	public void removeImage() {
		setBitmapUri(null);
		setBitmap(null);
		refreshImageView();
	}

}
