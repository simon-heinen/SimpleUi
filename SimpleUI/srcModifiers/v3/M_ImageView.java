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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class M_ImageView implements ModifierInterface, Target {

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
	private Integer maxHeightInPixel = null;
	private Integer maxWidthInPixel = null;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public M_ImageView() {
	}

	public M_ImageView(Context context, int drawableId) {
		Picasso.with(context).load(drawableId).into(this);
	}

	/**
	 * @param context
	 * @param imagePath
	 *            use {@link IO#toUri(File)} if you have a file instead of an
	 *            uri
	 */
	public M_ImageView(Context context, Uri imagePath) {
		Picasso.with(context).load(imagePath).into(this);
	}

	/**
	 * deprication info: read {@link M_ImageView#setBitmapUri(Uri)}
	 * 
	 * @param uri
	 */
	@Deprecated
	public M_ImageView(Uri uri) {
		setBitmapUri(uri);
	}

	public void setMaxHeightInPixel(Integer maxHeightInPixel) {
		this.maxHeightInPixel = maxHeightInPixel;
	}

	public void setMaxWidthInPixel(Integer maxWidthInPixel) {
		this.maxWidthInPixel = maxWidthInPixel;
	}

	/**
	 * deprication info: read {@link M_ImageView#setBitmapUri(Uri)}
	 * 
	 * @param imageFile
	 */
	@Deprecated
	public M_ImageView(File imageFile) {
		setBitmapUri(IO.toUri(imageFile));
	}

	private void setBitmap(Bitmap b) {
		if (b != bitmap && bitmap != null && !bitmap.isRecycled()) {
			oldBitmapToBeRecycled = bitmap;
		}
		bitmap = b;
	}

	/**
	 * Manually setting the bitmap object or uri is now deprecated, use the
	 * {@link Picasso} class instead like this:
	 * 
	 * Picasso.with(context).load(Uri.parse("http://wikipedia.de/img/logo.png"))
	 * .into(imageViewModifier);
	 * 
	 * @param bitmapUri
	 */
	@Deprecated
	public void setBitmapUri(Uri bitmapUri) {
		this.bitmapUri = bitmapUri;
	}

	public void load(Context context, File file) {
		setBitmapUri(IO.toUri(file));
		Picasso.with(context).load(file).into(this);
	}

	public void load(Context context, Uri uri) {
		setBitmapUri(uri);
		Picasso.with(context).load(uri).into(this);
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
		// LayoutParams params = new LayoutParams(
		// android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
		// android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		// linlay.setLayoutParams(params);
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
			Log.i(LOG_TAG,
					"refreshImageViewFromUiThread: Loading new bitmap for "
							+ bitmapUri);
			setBitmap(IO.loadBitmapFromUri(bitmapUri));
		}
		if (imageView != null) {
			if (bitmap != null && !bitmap.isRecycled()) {
				if (imageBorderColor != null && !bitmap.hasAlpha()) {
					imageView.setBackgroundColor(imageBorderColor);
				}
				LayoutParams p = new LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				imageView.setAdjustViewBounds(true);
				if (maxHeightInPixel != null) {
					imageView.setMaxHeight(maxHeightInPixel);
				}
				if (maxWidthInPixel != null) {
					imageView.setMaxWidth(maxWidthInPixel);
				}
				imageView.setPadding(imageBorderSizeInPixel,
						imageBorderSizeInPixel, imageBorderSizeInPixel,
						imageBorderSizeInPixel);
				imageView.setImageBitmap(bitmap);
				Log.i(LOG_TAG,
						"refreshImageViewFromUiThread: imageView has new bitmap="
								+ bitmap);
				imageView.setLayoutParams(p);
			} else {
				Log.i(LOG_TAG, "refreshImageViewFromUiThread: bitmap=" + bitmap
						+ " was recycled (or null)");
				if (imageBorderColor != null) {
					// else clear image border
					imageView.setBackgroundColor(Color.TRANSPARENT);
				}
				imageView.setImageBitmap(null);
			}
		} else {
			Log.i(LOG_TAG,
					"refreshImageViewFromUiThread: imageView null, can't set bitmap");
		}
		if (oldBitmapToBeRecycled != null && bitmap != oldBitmapToBeRecycled) {
			if (!oldBitmapToBeRecycled.isRecycled()) {
				oldBitmapToBeRecycled.recycle();
				Log.i(LOG_TAG,
						"refreshImageViewFromUiThread: Last bitmap was recycled");
			}
			oldBitmapToBeRecycled = null;
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
	 * deprication info: read {@link M_ImageView#setBitmapUri(Uri)}
	 * 
	 * @param bitmapUri
	 * @param bitmap
	 *            optional. the bitmap if it was already loaded from the image
	 *            uri. reloading the bitmap from the imageUri must result in the
	 *            same bitmap as this passed one!
	 */
	@Deprecated
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

	@Override
	public void onBitmapFailed() {
		Log.e(LOG_TAG, "Picasso could not load bitmap in this " + "modifier "
				+ this.getClass());
	}

	@Override
	public void onBitmapLoaded(Bitmap b, LoadedFrom arg1) {
		Log.d(LOG_TAG, "Image loaded from " + arg1 + ": " + b.getHeight() + "x"
				+ b.getWidth());
		setBitmap(b);
		refreshImageView();
	}
}
