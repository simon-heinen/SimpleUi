package v3;

import v2.simpleUi.ModifierInterface;
import v2.simpleUi.util.IO;
import v2.simpleUi.util.SimpleAsyncTask;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class M_ImageView implements ModifierInterface {

	protected static final String LOG_TAG = "M_ImageView";
	private String imageUrl;

	public M_ImageView(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public View getView(Context context) {
		ImageView v = new ImageView(context);
		if (imageUrl != null) {
			loadAndSetImage(v);
		}
		return v;
	}

	protected void loadAndSetImage(final ImageView v) {
		new SimpleAsyncTask() {

			private Bitmap bitmap;

			@Override
			public void onRun() {
				bitmap = loadBitmapFromUrl(imageUrl);
			}

			@Override
			public void onTaskFinished() {
				if (bitmap != null && bitmap.isRecycled()) {
					Log.e(LOG_TAG,
							"Tried to set recycled image to the image view");
					Thread.dumpStack();
				} else {
					v.setImageBitmap(bitmap);
				}
			}

		}.run();

	}

	@Override
	public boolean save() {
		return true;
	}

	public Bitmap loadBitmapFromUrl(String url) {
		return IO.loadBitmapFromURL(url);
	}

}
