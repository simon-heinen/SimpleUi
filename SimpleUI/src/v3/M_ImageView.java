package v3;

import v2.simpleUi.ModifierInterface;
import v2.simpleUi.util.IO;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class M_ImageView implements ModifierInterface {

	protected static final String LOG_TAG = "M_ImageView";
	private String imageUrl;
	private ImageView mImageView;
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
		setImageBitmap(bitmap);

		return mImageView;
	}

	public void setImageBitmap(final Bitmap bitmap) {
		myHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mImageView != null && bitmap != null && bitmap.isRecycled()) {
					Log.e(LOG_TAG,
							"Tried to set recycled image to the image view");
					Thread.dumpStack();
				} else {
					mImageView.setImageBitmap(bitmap);
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
