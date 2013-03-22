package v3;

import v2.simpleUi.ModifierInterface;
import v2.simpleUi.util.IO;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class M_ImageView implements ModifierInterface {

	protected static final String LOG_TAG = "M_ImageView";
	private String imageUrl;
	private ImageView mImageView;
	private Integer imageBorderColor = Color.parseColor("#F5F1DE");
	private int imageBorderSizeInPixel = 10;
	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public M_ImageView(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public M_ImageView() {
	}

	@Override
	public View getView(Context context) {
		mImageView = new ImageView(context);

		Bitmap bitmap = loadBitmapFromUrl(imageUrl);

		if (mImageView.getBackground() != null) {
			imageBorderColor = null;
		}

		setImageBitmap(bitmap);

		LinearLayout linlay = new LinearLayout(context);
		linlay.setGravity(Gravity.CENTER_HORIZONTAL);
		int p = DEFAULT_PADDING;
		linlay.setPadding(p, p, p, p);
		linlay.addView(mImageView);
		return linlay;
	}

	public void setImageBitmap(final Bitmap bitmap) {
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mImageView != null && bitmap != null) {
					if (bitmap.isRecycled()) {
						Log.e(LOG_TAG,
								"Tried to set recycled image to the image view");
						Thread.dumpStack();
					} else {
						if (imageBorderColor != null) {
							mImageView.setBackgroundColor(imageBorderColor);
						}
						mImageView.setPadding(imageBorderSizeInPixel,
								imageBorderSizeInPixel, imageBorderSizeInPixel,
								imageBorderSizeInPixel);
						mImageView.setImageBitmap(bitmap);
					}
				} else {
					if (imageBorderColor != null) {
						// else clear image border
						mImageView.setBackgroundColor(Color.TRANSPARENT);

					}
				}
			}
		});

	}

	@Override
	public boolean save() {
		return true;
	}

	public Bitmap loadBitmapFromUrl(String url) {
		if (url != null) {
			return IO.loadBitmapFromURL(url);
		}
		return null;
	}

}
