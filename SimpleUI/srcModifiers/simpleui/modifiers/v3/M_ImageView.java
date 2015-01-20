package simpleui.modifiers.v3;

import java.io.File;
import java.io.IOException;

import simpleui.customViews.ScalableImageView;
import simpleui.modifiers.ModifierInterface;
import simpleui.util.ColorUtils;
import simpleui.util.IO;
import simpleui.util.ImageTransform;
import simpleui.util.SimpleUiApplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

public class M_ImageView implements ModifierInterface, Target {

	private static final String LOG_TAG = "M_ImageView";
	private ImageView imageView;

	private Uri bitmapUri;
	private Bitmap bitmap;

	private Integer imageBorderColor;
	private int imageBorderSizeInPixel = 1;
	private Bitmap oldBitmapToBeRecycled;
	private TextView imageCaption;
	private String caption;
	private OnClickListener imageClickListener;
	private Integer maxHeightInPixel = null;
	private Integer maxWidthInPixel = null;
	private Context context;
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

	public M_ImageView(Uri uri) {
		setBitmapUri(uri);
	}

	public M_ImageView(Uri uri, String caption) {
		this(uri);
		setImageCaption(caption);
	}

	public void setMaxHeightInPixel(Integer maxHeightInPixel) {
		this.maxHeightInPixel = maxHeightInPixel;
	}

	public void setMaxWidthInPixel(Integer maxWidthInPixel) {
		this.maxWidthInPixel = maxWidthInPixel;
	}

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
		this.context = context;
		imageView = new ScalableImageView(context) {

			@Override
			protected void onAttachedToWindow() {
				new Thread(new Runnable() {

					@Override
					public void run() {
						refreshImageView();
					}
				}).start();
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
		RelativeLayout container = new RelativeLayout(context);
		container.addView(imageView);
		imageCaption = new TextView(context);
		LayoutParams p = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		imageCaption.setLayoutParams(p);
		float shadowSize = ImageTransform.dipToPixels(context, 1);
		float shaddowPos = ImageTransform.dipToPixels(context, 1);
		imageCaption.setShadowLayer(shadowSize, shaddowPos, shaddowPos,
				ColorUtils.getDefaultBackgroundColor(context, Color.BLACK));
		int m = (int) ImageTransform.dipToPixels(context, 10);
		imageCaption.setPadding(m, m, m, m);
		imageCaption.setTextAppearance(context,
				android.R.style.TextAppearance_Large);
		if (caption == null) {
			imageCaption.setVisibility(View.GONE);
		} else {
			setImageCaption(caption);
		}
		container.addView(imageCaption);
		if (imageBorderColor == null) {
			imageBorderColor = ColorUtils.getContrastVersionForColor(ColorUtils
					.getDefaultBackgroundColor(context, Color.DKGRAY));
		}
		return container;
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
			try {
				if (bitmap == null || bitmap.isRecycled()) {
					Log.d(LOG_TAG, "Loading bitmap via Picasso..");
					setBitmap(Picasso.with(getContext()).load(bitmapUri).get());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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
				imageView.setVisibility(View.VISIBLE);
				if (imageView.getBackground() == null && !bitmap.hasAlpha()) {
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
				imageView.setImageBitmap(null);
				imageView.setVisibility(View.INVISIBLE);
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

	private Context getContext() {
		if (context == null) {
			context = SimpleUiApplication.getContext();
		}
		return context;
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

	public void setImageBorderColor(Integer imageBorderColor) {
		this.imageBorderColor = imageBorderColor;
	}

	public void setImageBorderSizeInPixel(int imageBorderSizeInPixel) {
		this.imageBorderSizeInPixel = imageBorderSizeInPixel;
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
		Log.d(LOG_TAG, "setImage(Uri " + bitmapUri + ", Bitmap " + bitmap + ")");
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
