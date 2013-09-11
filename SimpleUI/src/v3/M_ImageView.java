package v3;

import java.io.File;

import tools.IO;
import v2.simpleUi.ModifierInterface;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class M_ImageView implements ModifierInterface {

	private static final String LOG_TAG = "M_ImageView";
	private ImageView imageView;

	private Uri bitmapUri;
	private Bitmap bitmap;

	private Integer imageBorderColor = Color.parseColor("#F5F1DE");
	private final int imageBorderSizeInPixel = 10;
	private Bitmap oldBitmapToBeRecycled;
	private TextView imageCaption;
	private String caption;
	private OnClickListener imageClickListener;
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
		imageView = new ImageView(context) {

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
		if (imageClickListener != null) {
			setImageClickListener(imageClickListener);
		}
		if (imageView.getBackground() != null) {
			imageBorderColor = null;
		}

		LinearLayout linlay = new LinearLayout(context);
		linlay.setGravity(Gravity.CENTER_HORIZONTAL);
		linlay.setOrientation(LinearLayout.VERTICAL);
		int p = DEFAULT_PADDING;
		linlay.setPadding(p, p, p, p);
		linlay.addView(imageView);
		imageCaption = new TextView(context);
		imageCaption.setGravity(Gravity.CENTER_HORIZONTAL);
		if (caption == null) {
			imageCaption.setVisibility(View.GONE);
		} else {
			setImageCaption(caption);
		}
		linlay.addView(imageCaption);
		return linlay;
	}

	public void setImageCaption(String newCaption) {
		this.caption = newCaption;
		myHandler.post(new Runnable() {

			@Override
			public void run() {
				if (imageCaption != null) {
					if (caption != null) {
						imageCaption.setText(caption);
						imageCaption.setVisibility(View.VISIBLE);
					} else {
						imageCaption.setVisibility(View.GONE);
					}
				}
			}
		});

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
		if (imageView != null) {
			if (bitmap != null) {
				if (imageBorderColor != null) {
					imageView.setBackgroundColor(imageBorderColor);
				}
				LayoutParams p = new LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				imageView.setLayoutParams(p);
				imageView.setPadding(imageBorderSizeInPixel,
						imageBorderSizeInPixel, imageBorderSizeInPixel,
						imageBorderSizeInPixel);
				imageView.setImageBitmap(bitmap);
			} else {
				if (imageBorderColor != null) {
					// else clear image border
					imageView.setBackgroundColor(Color.TRANSPARENT);
				}
				imageView.setImageBitmap(null);
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

	public void setImageClickListener(OnClickListener l) {
		imageClickListener = l;
		if (imageView != null) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					imageView.setOnClickListener(imageClickListener);
				}
			});
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
