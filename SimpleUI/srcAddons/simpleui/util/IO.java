package simpleui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import simpleui.commands.CommandInUiThread;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.squareup.picasso.Picasso;

/**
 * Android specific extensions to default {@link simpleui.util.IOHelper} class
 * 
 */
public class IO extends simpleui.util.IOHelper {

	private static final String LOG_TAG = "IO";

	public static Bitmap loadBitmapFromId(Context context, int bitmapId) {
		if (context == null) {
			Log.e(LOG_TAG, "Context was null!");
			return null;
		}
		InputStream is = context.getResources().openRawResource(bitmapId);
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		try {
			Bitmap b = BitmapFactory.decodeStream(is, null, bitmapOptions);
			Log.i(LOG_TAG, "image loaded: " + b);
			return b;
		} catch (Exception ex) {
			Log.e("bitmap loading exeption", "" + ex);
			return null;
		}
	}

	/**
	 * use {@link IO#convertStreamToString(InputStream)} instead
	 * 
	 * @param stream
	 * @return
	 */
	@Deprecated
	public static String convertInputStreamToString(InputStream stream) {
		if (stream == null) {
			return null;
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			StringBuilder sb = new StringBuilder();

			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			stream.close();
			return sb.toString();

		} catch (Exception e) {
			Log.e(LOG_TAG, "Could not convert input stream to string");
		}
		return null;
	}

	/**
	 * see {@link IO#loadBitmapFromFile(String)}
	 * 
	 * @param imagePath
	 * @param maxWidthInPixels
	 * @param maxHeightInPixels
	 * @return
	 */
	public static Bitmap loadBitmapFromFile(String imagePath,
			int maxWidthInPixels, int maxHeightInPixels) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		try {
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imagePath, o);
			o.inSampleSize = calculateInSampleSize(o, maxWidthInPixels,
					maxHeightInPixels);
			o.inPreferredConfig = Bitmap.Config.RGB_565;
			o.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(imagePath, o);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			try {
				return BitmapFactory.decodeFile(imagePath, o);
			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * any type of image can be imported this way
	 * 
	 * @param imagePath
	 *            for example "/sdcard/abc.PNG"
	 * @return null if the bitmap could not be loaded
	 */
	public static Bitmap loadBitmapFromFile(String imagePath) {
		int maxSize = 2048;
		return loadBitmapFromFile(imagePath, maxSize, maxSize);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both height and width larger than the requested height and
			// width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	/**
	 * The Eclipse UI editor cant preview ressources loaded from the assets
	 * folder so a dummy bitmap is used instead
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static Bitmap loadBitmapFromIdInCustomView(View v, int id) {
		if (v.isInEditMode() || id == 0) {
			return ImageTransform.createDummyBitmap();
		} else {
			return IO.loadBitmapFromId(v.getContext(), id);
		}
	}

	public static Bitmap loadBitmapFromJar(Context context, String path) {
		try {
			InputStream stream = Thread.currentThread().getContextClassLoader()
					.getResource(path).openStream();
			return BitmapFactory.decodeStream(stream);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Can't load bitmap from jar with path=" + path);

			try {
				return loadBitmapFromAssetsFolder(context, path);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap loadBitmapFromFile(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		return loadBitmapFromFile(file.toString());
	}

	/**
	 * @param filePath
	 *            e.g.
	 *            Environment.getExternalStorageDirectory().getAbsolutePath() +
	 *            File.separator +"test.jpg"
	 * @param b
	 * @param jpgQuality
	 *            e.g. 90
	 * @return
	 */
	public static boolean saveImageToFile(String filePath, Bitmap b,
			int jpgQuality) {
		if (b == null) {
			Log.e(LOG_TAG, "saveImageToFile: Passed bitmap was null!");
			return false;
		}
		try {
			File f = newFile(filePath);
			FileOutputStream out = new FileOutputStream(f);
			b.compress(Bitmap.CompressFormat.PNG, jpgQuality, out);
			out.close();
			return f.exists();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap loadBitmapFromURL(String url) {
		return loadBitmapFromUrl(SimpleUiApplication.getContext(), url);
	}

	public static Bitmap loadBitmapFromUrl(Context c, String url) {
		try {
			if (c != null) {
				return Picasso.with(c).load(url).get();
			} else {
				Log.w(LOG_TAG, "Context in loadBitmapFromUrl() "
						+ "was null, can't use Picasso");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			int length = connection.getContentLength();
			if (length > -1) {
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
				return BitmapFactory.decodeStream(connection.getInputStream(),
						null, bitmapOptions);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error while loading an image from an URL: ", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * turns any view (or view composition) in a bitmap
	 * 
	 * @param v
	 *            the view to transform into the bitmap
	 * @return the bitmap with the correct size of the view
	 */
	public static Bitmap loadBitmapFromView(View v) {
		v.clearFocus();
		v.setPressed(false);
		if (v.getMeasuredHeight() <= 0) {
			// first calc the size the view will need:
			v.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			// then create a bitmap to store the views drawings:
			Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(),
					v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			// wrap the bitmap:
			Canvas c = new Canvas(b);
			// set the view size to the mesured values:
			v.layout(0, 0, v.getWidth(), v.getHeight());
			// and draw the view onto the bitmap contained in the canvas:
			v.draw(c);
			return b;
		} else {
			Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);
			v.draw(c);
			return b;
		}

	}

	/**
	 * @param context
	 * @param id
	 *            something like "R.drawable.icon"
	 * @return
	 */
	public static Drawable loadDrawableFromId(Context context, int id)
			throws NotFoundException {
		return context.getResources().getDrawable(id);
	}

	public static void saveStringToExternalStorage(String filename,
			String textToSave) throws IOException {

		File file = newFile(filename);

		FileOutputStream foStream = new FileOutputStream(file);
		OutputStreamWriter stringOut = new OutputStreamWriter(foStream);
		stringOut.write(textToSave);
		stringOut.close();
		foStream.close();
	}

	public static void saveSerializableToPrivateStorage(Context context,
			String filename, Serializable objectToSave) throws IOException {
		FileOutputStream fileOut = context.openFileOutput(filename,
				Context.MODE_PRIVATE);
		saveSerializableToStream(objectToSave, fileOut);
	}

	public static Object loadSerializableFromPrivateStorage(Context context,
			String filename) throws StreamCorruptedException,
			OptionalDataException, IOException, ClassNotFoundException {
		FileInputStream fiStream = context.openFileInput(filename);
		return loadSerializableFromStream(fiStream);
	}

	public static class Settings {

		Context context;
		private final String mySettingsName;
		/**
		 * The editor is stored as a field because every
		 * {@link SharedPreferences}.edit() call will create a new
		 * {@link Editor} object and this way resources are saved
		 */
		private Editor e;
		private int mode = Context.MODE_PRIVATE;

		public Settings(Context target) {
			context = target;
			mySettingsName = "settings";
		}

		public Settings(Context target, String settingsFileName) {
			context = target;
			mySettingsName = settingsFileName;
		}

		/**
		 * @param mode
		 *            default value is {@link Context}.MODE_PRIVATE
		 */
		public void setMode(int mode) {
			this.mode = mode;
		}

		public String loadString(String key) {
			return context.getSharedPreferences(mySettingsName, mode)
					.getString(key, null);
		}

		/**
		 * @param key
		 * @param defaultValue
		 *            the value which will be returned if there was no value
		 *            found for the given key
		 * @return
		 */
		public boolean loadBool(String key, boolean defaultValue) {
			return context.getSharedPreferences(mySettingsName, mode)
					.getBoolean(key, defaultValue);
		}

		/**
		 * @param key
		 * @param defaultValue
		 *            the value which will be returned if there was no value
		 *            found for the given key
		 * @return
		 */
		public int loadInt(String key, int defaultValue) {
			return context.getSharedPreferences(mySettingsName, mode).getInt(
					key, defaultValue);
		}

		public boolean storeString(String key, String value) {
			if (e == null) {
				e = context.getSharedPreferences(mySettingsName, mode).edit();
			}
			e.putString(key, value);
			return e.commit();
		}

		public boolean storeBool(String key, boolean value) {
			if (e == null) {
				e = context.getSharedPreferences(mySettingsName, mode).edit();
			}
			e.putBoolean(key, value);
			return e.commit();
		}

		public boolean storeInt(String key, int value) {
			if (e == null) {
				e = context.getSharedPreferences(mySettingsName, mode).edit();
			}
			e.putInt(key, value);
			return e.commit();
		}
	}

	public static File getSDCardDirectory() {
		return Environment.getExternalStorageDirectory();
	}

	/**
	 * @Deprecated till android dependency fixed
	 * 
	 * @param oldPath
	 *            e.g. /myFolder/test.txt
	 * @param newName
	 *            e.g. oldTest.txt
	 * @return
	 */
	@Deprecated
	public static boolean renameFile(String oldPath, String newName) {
		File source = new File(Environment.getExternalStorageDirectory(),
				oldPath);
		File dest = new File(source.getParent(), newName);
		return source.renameTo(dest);
	}

	public static FileReader loadFileReaderFromAssets(Context c,
			String relativePathInAssetsFolder) {
		try {
			return new FileReader(c.getAssets()
					.openFd(relativePathInAssetsFolder).getFileDescriptor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Deprecated, better use {@link Picasso} in combination with
	 * {@link IO#loadFileFromAssets(String)}
	 * 
	 * @param context
	 * @param fileName
	 *            e.g. "x/a.jpg" if the image is located at "assets/x/a.jpg"
	 * @return
	 */
	@Deprecated
	public static Bitmap loadBitmapFromAssetsFolder(Context context,
			String fileName) {
		try {
			Log.e(LOG_TAG, "Trying to load " + fileName
					+ " from assets folder!");
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			return BitmapFactory.decodeStream(context.getAssets()
					.open(fileName), null, bitmapOptions);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Could not load " + fileName
					+ " from assets folder!");
		}
		return null;
	}

	/**
	 * @param relativePathInAssetsFolder
	 *            something like "folderX/fileY.txt" if you have a folder in
	 *            your assets folder folderX which contains a fileY.txt
	 * @return a file object which does not behave normally, e.g. file.exists()
	 *         will always return null! But {@link Picasso} e.g. still can
	 *         handle the file correctly
	 */
	public static Uri loadFileFromAssets(String relativePathInAssetsFolder) {
		if (relativePathInAssetsFolder.startsWith("assets")
				|| relativePathInAssetsFolder.startsWith("/assets")) {
			Log.i(LOG_TAG, "Found string assets in file path='"
					+ relativePathInAssetsFolder + "', will clean the string");
			relativePathInAssetsFolder = relativePathInAssetsFolder
					.replaceFirst("assets", "");
			while (relativePathInAssetsFolder.startsWith("/")) {
				relativePathInAssetsFolder = relativePathInAssetsFolder
						.replaceFirst("/", "");
			}
		}

		if (!relativePathInAssetsFolder.startsWith("/")) {
			relativePathInAssetsFolder = "/" + relativePathInAssetsFolder;
		}
		return Uri.parse("file:///android_asset" + relativePathInAssetsFolder);
	}

	public interface FileFromUriListener {

		void onStart();

		void onStop(File downloadedFile);

		void onError(Exception e);

		/**
		 * @return every time this method is called false can be returned to
		 *         stop the download process
		 */
		boolean cancelDownload();

		/**
		 * @param percent
		 *            value between 0 to 100
		 */
		void onProgress(int percent);

	}

	/**
	 * Synchronously loads data into a {@link File} from the specified
	 * {@link Uri}
	 * 
	 * @param sourceUri
	 *            the {@link Uri} where the content should come from
	 * @param targetFolder
	 *            the folder where the content should be stored in
	 * @param fallbackFileName
	 *            the fallback filename if the online resource does not provide
	 *            its filename
	 * @param l
	 *            can be null, if no error or update information is needed
	 * @return the downloaded {@link File} or null if an error happened
	 */
	public static File loadFileFromUri(Uri sourceUri, File targetFolder,
			String fallbackFileName, FileFromUriListener l) {
		try {
			if (!targetFolder.exists() && !targetFolder.mkdirs()) {
				if (l != null) {
					l.onError(new Exception("Could not create folder "
							+ targetFolder));
				}
				return null;
			}

			URL url = new URL(sourceUri.toString());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.connect();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (l != null) {
					l.onError(new Exception("Server returned HTTP "
							+ connection.getResponseCode() + " "
							+ connection.getResponseMessage()));
				}
				return null;
			}

			// this will be useful to display download percentage
			// might be -1: server did not report the length
			int fileLength = connection.getContentLength();

			String raw = connection.getHeaderField("Content-Disposition");
			// raw = "attachment; filename=abc.jpg"
			if (raw != null) {
				System.out.println("raw=" + raw);
				Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
				Matcher regexMatcher = regex.matcher(raw);
				if (regexMatcher.find()) {
					fallbackFileName = regexMatcher.group();
				} else if (raw.contains("=")) {
					fallbackFileName = ""
							+ raw.subSequence(raw.lastIndexOf("=") + 1,
									raw.length());
					fallbackFileName = fallbackFileName.trim();
				}

			}
			File targetFile = new File(targetFolder, fallbackFileName);
			InputStream input = connection.getInputStream();
			OutputStream output = new FileOutputStream(targetFile);

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			if (l != null) {
				l.onStart();
			}
			while ((count = input.read(data)) != -1) {
				// allow canceling with back button
				if (l != null && l.cancelDownload()) {
					input.close();
					output.close();
					return null;
				}
				total += count;
				if (l != null && fileLength > 0) {
					l.onProgress((int) (total * 100 / fileLength));
				}
				output.write(data, 0, count);
			}
			if (output != null) {
				output.close();
			}
			if (input != null) {
				input.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
			if (l != null) {
				l.onStop(targetFile);
			}
			return targetFile;
		} catch (Exception e) {
			if (l != null) {
				l.onError(e);
			}
			return null;
		}
	}

	public interface AssetCopyCallback {
		void onAssetCopyingDone(Context c, File targetFolder);
	}

	/**
	 * @param c
	 * @param sourceAssetsFolder
	 * @param targetFolder
	 * @param replaceExistingTargetFolder
	 *            if true, the existing folder will be deleted, this can be
	 *            usefull if the content of the assets folder changed
	 * @param callback
	 */
	public static void copyAssetsWithCallback(Context c,
			String sourceAssetsFolder, File targetFolder,
			boolean replaceExistingTargetFolder,
			final AssetCopyCallback callback) {
		if (replaceExistingTargetFolder && targetFolder.exists()) {
			if (!deleteFolder(targetFolder)) {
				Log.e(LOG_TAG, "Could not delete " + targetFolder);
			}
		}
		if (!targetFolder.exists()) {
			copyFilesFromAssetsToSdCard(c, sourceAssetsFolder, targetFolder,
					callback);
		} else {
			callback.onAssetCopyingDone(c, targetFolder);
		}
	}

	public static void copyFilesFromAssetsToSdCard(final Context c,
			final String sourceAssetsFolder, final File targetFolder,
			final AssetCopyCallback callback) {
		final ProgressScreen p = new ProgressScreen() {

			@Override
			public boolean onAbortRequest() {
				return false;
			}
		};
		p.start(c);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (IO.copyAssets(c.getAssets(), sourceAssetsFolder,
							Environment.getExternalStorageDirectory())) {
						new CommandInUiThread() {

							@Override
							public void executeInUiThread() {
								callback.onAssetCopyingDone(c, targetFolder);
							}
						}.execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				p.finish();
			}
		}).start();
	}

	/**
	 * 
	 * Info: prior to Android 2.3, any compressed asset file with an
	 * uncompressed size of over 1 MB cannot be read from the APK. So this
	 * should only be used if the device has android 2.3 or later running!
	 * 
	 * @param c
	 * @param targetFolder
	 *            e.g. {@link Environment#getExternalStorageDirectory()}
	 * @throws Exception
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static boolean copyAssets(AssetManager assetManager,
			File targetFolder) throws Exception {
		Log.i(LOG_TAG, "Copying files from assets to folder " + targetFolder);
		return copyAssets(assetManager, "", targetFolder);
	}

	/**
	 * The files will be copied at the location targetFolder+path so if you
	 * enter path="abc" and targetfolder="sdcard" the files will be located in
	 * "sdcard/abc"
	 * 
	 * @param assetManager
	 * @param path
	 * @param targetFolder
	 * @return
	 * @throws Exception
	 */
	public static boolean copyAssets(AssetManager assetManager, String path,
			File targetFolder) throws Exception {
		Log.i(LOG_TAG, "Copying " + path + " to " + targetFolder);
		String sources[] = assetManager.list(path);
		if (sources.length == 0) { // its not a folder, so its a file:
			copyAssetFileToFolder(assetManager, path, targetFolder);
		} else { // its a folder:
			if (path.startsWith("images") || path.startsWith("sounds")
					|| path.startsWith("webkit")) {
				Log.i(LOG_TAG, "  > Skipping " + path);
				return false;
			}
			File targetDir = new File(targetFolder, path);
			targetDir.mkdirs();
			for (String source : sources) {
				String fullSourcePath = path.equals("") ? source : (path
						+ File.separator + source);
				copyAssets(assetManager, fullSourcePath, targetFolder);
			}
		}
		return true;
	}

	private static void copyAssetFileToFolder(AssetManager assetManager,
			String fullAssetPath, File targetBasePath) throws IOException {
		InputStream in = assetManager.open(fullAssetPath);
		OutputStream out = new FileOutputStream(new File(targetBasePath,
				fullAssetPath));
		byte[] buffer = new byte[16 * 1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		in.close();
		out.flush();
		out.close();
	}

	public static Uri toUri(File file) {
		return Uri.fromFile(file);
	}

	public static File toFile(Uri uri) {
		if (uri == null) {
			return null;
		}
		return new File(uri.getPath());
	}

	public static Bitmap loadBitmapFromUri(Uri uri) {
		if (uri == null) {
			return null;
		}
		return loadBitmapFromFile(toFile(uri));
	}

}
