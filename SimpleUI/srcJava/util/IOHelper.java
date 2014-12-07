package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IOHelper {

	private static final String LOG_TAG = "IO";

	/**
	 * @param filename
	 *            something like "/sdcard/test.txt"
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws OptionalDataException
	 * @throws StreamCorruptedException
	 */
	public static Object loadSerializableFromExternalStorage(String filename)
			throws StreamCorruptedException, OptionalDataException,
			IOException, ClassNotFoundException {
		FileInputStream fiStream = new FileInputStream(filename);
		return loadSerializableFromStream(fiStream);
	}

	public static boolean copy(String sourceName, String targetName) {
		File source = new File(sourceName);
		File dest = new File(targetName);
		try {
			copy(source, dest);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		if (is == null) {
			Log.e(LOG_TAG, "Passed input stream was null");
			return null;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		reader.mark(1);
		if (reader.read() != 0xFEFF) {
			reader.reset();
		}
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	public static String loadStringFromFile(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		String ret = convertStreamToString(fin);
		// Make sure you close all streams.
		fin.close();
		return ret;
	}

	/**
	 * @param newDirectory
	 * @return false if the folder already existed or could not be created
	 */
	public static boolean createFolder(String newDirectory) {
		return new File(newDirectory).mkdirs();
	}

	/**
	 * @param filename
	 *            something like
	 *            Environment.getExternalStorageDirectory().getAbsolutePath() +
	 *            File.separator +"test.txt"
	 * @param objectToSave
	 * @throws IOException
	 */
	public static void saveSerializableToExternalStorage(String filename,
			Serializable objectToSave) throws IOException {
		File file = newFile(filename);
		saveSerializableToExternalStorage(file, objectToSave);
	}

	public static void saveSerializableToExternalStorage(File file,
			Serializable objectToSave) throws FileNotFoundException,
			IOException {
		FileOutputStream foStream = new FileOutputStream(file);
		saveSerializableToStream(objectToSave, foStream);
	}

	public static File newFile(String filePath) throws IOException {
		return newFile(new File(filePath));
	}

	public static File newFile(File f) throws IOException {
		if (!f.exists()) {
			if (!f.getParentFile().exists()) {
				Log.i(LOG_TAG, "Trying to create dir "
						+ f.getParentFile().getAbsolutePath());
				if (!f.getParentFile().mkdirs()) {
					Log.w(LOG_TAG, "Cannot create dir "
							+ f.getParentFile().getAbsolutePath());
				}
			}
			f.createNewFile();
		}
		return f;
	}

	/**
	 * http://stackoverflow.com/questions/5715104/copy-files-from-a-folder-of-sd
	 * -card-into-another-folder-of-sd-card
	 * 
	 * If targetLocation does not exist, it will be created.
	 * 
	 * @param sourceLocation
	 * @param targetLocation
	 * @throws IOException
	 */
	public static void copy(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists() && !targetLocation.mkdirs()) {
				throw new IOException("Cannot create dir "
						+ targetLocation.getAbsolutePath());
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copy(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {
			// make sure the directory we plan to store the recording in exists
			File directory = targetLocation.getParentFile();
			if (directory != null && !directory.exists() && !directory.mkdirs()) {
				throw new IOException("Cannot create dir "
						+ directory.getAbsolutePath());
			}

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static List<File> getFilesInPath(String path) {
		return getFilesInPath(new File(path));
	}

	public static List<File> getFilesInPath(File directory) {
		return Arrays.asList(directory.listFiles());
	}

	protected static void saveSerializableToStream(Serializable objectToSave,
			FileOutputStream foStream) throws IOException {
		if (objectToSave == null) {
			throw new IOException("objectToSave was null, will not save this");
		}
		GZIPOutputStream gzioStream = new GZIPOutputStream(foStream);
		ObjectOutputStream outStream = new ObjectOutputStream(gzioStream);
		outStream.writeObject(objectToSave);
		outStream.flush();
		outStream.close();
		gzioStream.close();
		foStream.close();
	}

	protected static Object loadSerializableFromStream(FileInputStream fiStream)
			throws IOException, StreamCorruptedException,
			OptionalDataException, ClassNotFoundException {
		GZIPInputStream gzipStream = new GZIPInputStream(fiStream);
		ObjectInputStream inStream = new ObjectInputStream(gzipStream);
		Object loadedObject = inStream.readObject();
		inStream.close();
		if (loadedObject == null) {
			throw new ClassNotFoundException(
					"Class found but loaded object was null");
		}
		return loadedObject;
	}
}
