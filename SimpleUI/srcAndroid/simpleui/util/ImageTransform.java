package simpleui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class ImageTransform {

	private static final String LOG_TAG = "ImageTransform";

	/**
	 * @param bitmap
	 *            the source
	 * @param factor
	 *            should be between 2f (very visible round corners) and 20f
	 *            (nearly no round corners)
	 * @return the result
	 */
	public static Bitmap createBitmapWithRoundCorners(Bitmap bitmap,
			float factor) {

		if (bitmap == null) {
			return null;
		}

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap result = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(result);
		Rect rect = new Rect(0, 0, width, height);
		RectF roundCornerFrameRect = new RectF(rect);
		float cornerRadius = width / factor;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawRoundRect(roundCornerFrameRect, cornerRadius, cornerRadius,
				paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return result;
	}

	public static Bitmap createDummyBitmap() {
		int size = 128;
		Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
		Canvas c = new Canvas(b);
		Paint p = new Paint();

		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(20);
		int alpha = 255;
		p.setColor(Color.rgb(50, 0, 0));
		p.setAlpha(alpha);
		c.drawLine(0, 0, size, size, p);
		p.setColor(Color.BLUE);
		p.setAlpha(alpha);
		c.drawLine(0, size, size, 0, p);
		p.setColor(Color.RED);
		p.setAlpha(alpha);
		c.drawLine(0, size / 2, size, size / 2, p);
		p.setColor(Color.YELLOW);
		p.setAlpha(alpha);
		c.drawLine(size / 2, 0, size / 2, size, p);

		float[] filterKernel = { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
		// improveSaturation(b, b, filterKernel, 1, 1, 1, 255,
		// 255);

		return b;
	}

	public static Bitmap createDummyBitmap2(View v) {
		int size = 128;
		Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
		Canvas c = new Canvas(b);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.BLUE);
		p.setStyle(Paint.Style.FILL);
		p.setStrokeWidth(10);
		drawCircle(v, c, size / 2, size / 2, size / 2, p);
		return b;
	}

	/**
	 * use {@link ImageTransform#dipToPixels(Resources, Integer)} instead
	 * 
	 * @param sizeInDip
	 * @return size in pixels
	 */
	@Deprecated
	public static float dipToPixels(View v, float sizeInDip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				sizeInDip, v.getResources().getDisplayMetrics());
	}

	/**
	 * Converts the dip into its equivalent pixel size
	 * 
	 * @param resources
	 *            get the ressources object e.g. via context.getRessources or
	 *            view.getRessources
	 * @param sizeInDip
	 * @return
	 */
	public static float dipToPixels(Resources resources, Integer sizeInDip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				sizeInDip, resources.getDisplayMetrics());
	}

	/**
	 * Converts the dip into its equivalent pixel size
	 * 
	 * @param context
	 * @param sizeInDip
	 * @return
	 */
	public static float dipToPixels(Context context, Integer sizeInDip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				sizeInDip, context.getResources().getDisplayMetrics());
	}

	@TargetApi(Build.VERSION_CODES.DONUT)
	public static float PixelsToDip(Context context, int px) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	/**
	 * Use this method instead of
	 * {@link Canvas#drawCircle(float, float, float, Paint)} or the Eclipse UI
	 * Editor preview will be incorrect
	 * 
	 * @param canvas
	 * @param cx
	 * @param cy
	 * @param radius
	 * @param paint
	 */
	public static void drawCircle(View v, Canvas canvas, float cx, float cy,
			float radius, Paint paint) {
		if (v.isInEditMode()) {
			RectF arcRect = new RectF(cx - radius, cy - radius, cx + radius, cy
					+ radius);
			// Draw the Minutes-Arc into that rectangle
			canvas.drawArc(arcRect, -90, 360, false, paint);
		} else {
			canvas.drawCircle(cx, cy, radius, paint);
		}

	}

	/**
	 * TODO compare to createBitmapWithRoundCorners(..) and combine to one
	 * method
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	@Deprecated
	public static Bitmap createRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xff424242);
		canvas.drawRoundRect(new RectF(rect), pixels, pixels, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * @param bitmap
	 *            the bitmap that has to be stored
	 * @param destinationFile
	 *            to target file path
	 * @param imageQuality
	 *            a number between 0 (bad quality) and 100 (best quality)
	 */
	public static void tryToStoreBitmapToTargetFile(Bitmap bitmap,
			File destinationFile, int imageQuality) {
		try {
			if (!destinationFile.exists()) {
				Log.i(LOG_TAG, "File did not exist so creating a new one at "
						+ destinationFile);
				destinationFile.getParentFile().mkdirs();
				if (destinationFile.createNewFile()) {
					Log.i(LOG_TAG, "    > new file created");
				} else {
					Log.w(LOG_TAG, "    > file did already exist");
				}
			}
			// override bitmap on external storage
			FileOutputStream out = new FileOutputStream(destinationFile);
			if (destinationFile.getPath().toUpperCase().endsWith("PNG")) {
				bitmap.compress(Bitmap.CompressFormat.PNG, imageQuality, out);
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This will scale the image first to add soft corners
	 * 
	 * @param bitmap
	 * @param angle
	 * @param smoothingFactor
	 *            try 1.5f; (1 would be no smoothing at all)
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int angle,
			float smoothingFactor) {
		Bitmap result = ImageTransform.resizeBitmap(bitmap, bitmap.getHeight()
				* smoothingFactor, bitmap.getWidth() * smoothingFactor);
		result = rotateBitmap(result, angle);
		result = ImageTransform.resizeBitmap(result, result.getHeight()
				/ smoothingFactor, result.getWidth() / smoothingFactor);
		return result;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, bitmap.getConfig());
		Canvas canvas = new Canvas(result);
		Matrix matrix = new Matrix();
		matrix.setRotate(angle, width / 2, height / 2);
		Paint p = new Paint();
		p.setAntiAlias(true);
		canvas.drawBitmap(bitmap, matrix, p);
		return result;
	}

	public static Bitmap makeSquare(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width == height) {
			return bitmap;
		}

		int size = width > height ? width : height;

		Bitmap newB = Bitmap.createBitmap(size, size, bitmap.getConfig());
		Canvas c = new Canvas(newB);

		int left = 0;
		int top = 0;

		if (width < height) {
			left = Math.abs((width - height) / 2);
		} else {
			top = Math.abs((width - height) / 2);
		}

		c.drawBitmap(bitmap, left, top, new Paint());

		return newB;
	}

	public static Bitmap addMargin(Bitmap bitmap, int marginSize) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap newB = Bitmap.createBitmap(width + 2 * marginSize, height + 2
				* marginSize, bitmap.getConfig());
		Canvas c = new Canvas(newB);
		c.drawBitmap(bitmap, marginSize, marginSize, new Paint());

		return newB;
	}

	// public static Bitmap resizeBitmap(Bitmap bitmap, float newHeight,
	// float newWidth) {
	// int width = bitmap.getWidth();
	// int height = bitmap.getHeight();
	// float scaleWidth = (newWidth) / width;
	// float scaleHeight = (newHeight) / height;
	// Matrix matrix = new Matrix();
	// matrix.postScale(scaleWidth, scaleHeight);
	// return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	// }

	public static Bitmap resizeBitmap(Bitmap takenBitmap, float maxHeight,
			float maxWidth) {
		return resizeBitmap(takenBitmap, maxWidth, maxHeight, new Matrix());
	}

	/**
	 * @param context
	 * @param uri
	 *            the uri which represents the path to the image. You can user
	 *            {@link Uri#fromFile(File)} e.g.
	 * @param width
	 *            the max width of the image
	 * @param height
	 *            the max height of the image
	 * @return the scaled image
	 */
	public static Bitmap getBitmapFromUri(Context context, Uri uri, int width,
			int height) {
		InputStream in = null;
		try {
			int IMAGE_MAX_SIZE = Math.max(width, height);
			in = context.getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(IMAGE_MAX_SIZE
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// adjust sample size such that the image is bigger than the result
			scale -= 1;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			in = context.getContentResolver().openInputStream(uri);
			Bitmap b = BitmapFactory.decodeStream(in, null, o2);
			in.close();

			// scale bitmap to desired size
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, width, height,
					false);

			// free memory
			if (b != scaledBitmap) {
				b.recycle();
			}

			return scaledBitmap;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * will use the file path to detect how the image has to be rotated,
	 * additionally rescales the bitmap to a better size for displaying
	 * 
	 * @param context
	 * @param takenBitmap
	 *            is automatically recycled after it has been transformed
	 * @param imageUri
	 *            pass the uri to the bitmap if it is available, else pass null.
	 *            will be recycled on success and the new bitmap object will be
	 *            returned!
	 * @param maxWidth
	 *            of the new created bitmap e.g. 640
	 * @param maxHeight
	 *            of the new created bitmap e.g. 480
	 * @return
	 */
	public static Bitmap rotateAndResizeBitmap(Context context,
			Bitmap takenBitmap, Uri imageUri, float maxWidth, float maxHeight) {
		Matrix matrix = new Matrix();
		if (imageUri != null) {
			float rotation = ImageTransform.rotationForImage(context, imageUri);
			if (rotation != 0f) {
				matrix.preRotate(rotation);
			}
		}
		return resizeBitmap(takenBitmap, maxWidth, maxHeight, matrix);
	}

	private static Bitmap resizeBitmap(Bitmap takenBitmap, float maxWidth,
			float maxHeight, Matrix matrix) {
		float width = takenBitmap.getWidth();
		float height = takenBitmap.getHeight();
		float newWidth = maxWidth;
		if (width < height) {
			newWidth = maxHeight;
		}
		float newHeight = height / width * newWidth;
		float scaleWidth = (newWidth) / width;
		float scaleHeight = (newHeight) / height;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBitmap = Bitmap.createBitmap(takenBitmap, 0, 0,
				takenBitmap.getWidth(), takenBitmap.getHeight(), matrix, true);
		if (newBitmap != takenBitmap) {
			takenBitmap.recycle();
		}
		return newBitmap;
	}

	/**
	 * http://mobisocial.stanford.edu/news/2011/08/rotating-images-in-android/
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	@TargetApi(5)
	private static float rotationForImage(Context context, Uri uri) {
		if (uri.getScheme().equals("content")) {
			String[] projection = { Images.ImageColumns.ORIENTATION };
			Cursor c = context.getContentResolver().query(uri, projection,
					null, null, null);
			if (c.moveToFirst()) {
				return c.getInt(0);
			}
		} else if (uri.getScheme().equals("file")) {
			try {
				ExifInterface exif = new ExifInterface(uri.getPath());
				int rotation = (int) exifOrientationToDegrees(exif
						.getAttributeInt(ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_NORMAL));
				return rotation;
			} catch (IOException e) {
				Log.e(LOG_TAG, "Error checking exif", e);
			}
		}
		return 0f;
	}

	private static float exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	/**
	 * From
	 * http://android-developers.blogspot.com/2012/01/levels-in-renderscript
	 * .html
	 * 
	 * TODO add description for parameters
	 * 
	 * @param sourceBitmap
	 * @param resultBitmap
	 * @param filterKernel
	 *            has to be a 3x3 matrix so float[9]. The kernal which will keep
	 *            all color values like they are is filterKernel={1,0,0, 0,1,0,
	 *            0,0,1}.
	 * @param mOverInWMinInB
	 *            from 0 to 1
	 * @param gammaValue
	 *            from 0 to 1
	 * @param mOutWMinOutB
	 *            from 0 to 1
	 * @param mInBlack
	 *            from 0 to 255
	 * @param mOutBlack
	 *            from 0 to 255
	 */
	public static void improveSaturation(Bitmap sourceBitmap,
			Bitmap resultBitmap, float[] filterKernel, float mOverInWMinInB,
			float gammaValue, float mOutWMinOutB, float mInBlack,
			float mOutBlack) {
		int[] mInPixels = new int[sourceBitmap.getHeight()
				* sourceBitmap.getWidth()];
		int[] mOutPixels = new int[resultBitmap.getHeight()
				* resultBitmap.getWidth()];
		sourceBitmap.getPixels(mInPixels, 0, sourceBitmap.getWidth(), 0, 0,
				sourceBitmap.getWidth(), sourceBitmap.getHeight());

		for (int i = 0; i < mInPixels.length; i++) {
			float r = mInPixels[i] & 0xff;
			float g = (mInPixels[i] >> 8) & 0xff;
			float b = (mInPixels[i] >> 16) & 0xff;

			float tr = r * filterKernel[0] + g * filterKernel[3] + b
					* filterKernel[6];
			float tg = r * filterKernel[1] + g * filterKernel[4] + b
					* filterKernel[7];
			float tb = r * filterKernel[2] + g * filterKernel[5] + b
					* filterKernel[8];
			r = tr;
			g = tg;
			b = tb;

			if (r < 0.f) {
				r = 0.f;
			}
			if (r > 255.f) {
				r = 255.f;
			}
			if (g < 0.f) {
				g = 0.f;
			}
			if (g > 255.f) {
				g = 255.f;
			}
			if (b < 0.f) {
				b = 0.f;
			}
			if (b > 255.f) {
				b = 255.f;
			}

			r = (r - mInBlack) * mOverInWMinInB;
			g = (g - mInBlack) * mOverInWMinInB;
			b = (b - mInBlack) * mOverInWMinInB;

			if (gammaValue != 1.0f) {
				r = (float) java.lang.Math.pow(r, gammaValue);
				g = (float) java.lang.Math.pow(g, gammaValue);
				b = (float) java.lang.Math.pow(b, gammaValue);
			}

			r = (r * mOutWMinOutB) + mOutBlack;
			g = (g * mOutWMinOutB) + mOutBlack;
			b = (b * mOutWMinOutB) + mOutBlack;

			if (r < 0.f) {
				r = 0.f;
			}
			if (r > 255.f) {
				r = 255.f;
			}
			if (g < 0.f) {
				g = 0.f;
			}
			if (g > 255.f) {
				g = 255.f;
			}
			if (b < 0.f) {
				b = 0.f;
			}
			if (b > 255.f) {
				b = 255.f;
			}

			mOutPixels[i] = ((int) r) + (((int) g) << 8) + (((int) b) << 16)
					+ (mInPixels[i] & 0xff000000);
		}

		resultBitmap.setPixels(mOutPixels, 0, resultBitmap.getWidth(), 0, 0,
				resultBitmap.getWidth(), resultBitmap.getHeight());
	}

	/**
	 * @param targetBitmap
	 * @param type
	 *            1 is Green-Blue, 2 is Red-Blue, 3 is Red - Green
	 */
	public static void switchColors(Bitmap targetBitmap, int type) {
		int width = targetBitmap.getWidth();
		int height = targetBitmap.getHeight();
		int[] srcPixels = new int[width * height];
		targetBitmap.getPixels(srcPixels, 0, width, 0, 0, width, height);
		int[] destPixels = new int[width * height];
		switch (type) {
		case 1:
			swapGreenBlue(srcPixels, destPixels);
			break;
		case 2:
			swapRedBlue(srcPixels, destPixels);
			break;
		case 3:
			swapRedGreen(srcPixels, destPixels);
			break;
		}
		targetBitmap.setPixels(destPixels, 0, width, 0, 0, width, height);
	}

	private static void swapGreenBlue(int[] src, int[] dest) {
		for (int i = 0; i < src.length; i++) {
			dest[i] = (src[i] & 0xffff0000) | ((src[i] & 0x000000ff) << 8)
					| ((src[i] & 0x0000ff00) >> 8);
		}
	}

	private static void swapRedBlue(int[] src, int[] dest) {
		for (int i = 0; i < src.length; i++) {
			dest[i] = (src[i] & 0xff00ff00) | ((src[i] & 0x000000ff) << 16)
					| ((src[i] & 0x00ff0000) >> 16);
		}
	}

	private static void swapRedGreen(int[] src, int[] dest) {
		for (int i = 0; i < src.length; i++) {
			dest[i] = (src[i] & 0xff0000ff) | ((src[i] & 0x0000ff00) << 8)
					| ((src[i] & 0x00ff0000) >> 8);
		}
	}

}
