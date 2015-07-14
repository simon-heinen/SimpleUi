package simpleui.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class IOHelper {

	private static final String LOG_TAG = IOHelper.class.getSimpleName();

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

	/**
	 * searched for files in a specified directory based on their names, will
	 * also accept partial names and return such files (e.g. if a file is named
	 * "abc.jpg" the file will also be found if "abc" is passed as a file name.
	 * The first file which will be found will be returned
	 * 
	 * @param baseDir
	 * @param fileName
	 * @param searchInSubdirectories
	 * @return a file (not a directory) or null if no file could be found
	 */
	public static File findFileIn(File baseDir, String fileName,
			boolean searchInSubdirectories) {
		List<File> files = getFilesInPath(baseDir);
		if (files == null || files.isEmpty()) {
			return null;
		}
		for (File file : files) {
			if (file.getName().equals(fileName) && !file.isDirectory()) {
				return file;
			}
		}
		for (File file : files) {
			if (file.getName().contains(fileName) && !file.isDirectory()) {
				return file;
			}
		}
		if (searchInSubdirectories) {
			for (File file : files) {
				if (file.isDirectory()) {
					File f = findFileIn(file, fileName, searchInSubdirectories);
					if (f != null) {
						return f;
					}
				}
			}
		}
		return null;
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

	public interface FileFromUriListener {

		/**
		 * @param fileName
		 *            the name of the file which will be downloaded
		 * @param lastModifiedTimestamp
		 *            the timestamp when the file was last changed, can be 0 if
		 *            not available
		 * @return true to continue the process, false to abort it
		 * @param fileSizeOnServer
		 *            file size in bytes on server, check additionally to the
		 *            lastModifiedTimestamp, can be null if unknown
		 * @return the cached file if available, or null if the file should be
		 *         downloaded
		 */
		File onGetCachedVersion(String fileName, long lastModifiedTimestamp,
				Integer fileSizeOnServer);

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
	 * {@link URL}
	 * 
	 * @param sourceUri
	 *            the {@link URL} where the content should come from
	 * @param targetFolder
	 *            the folder where the content should be stored in
	 * @param fallbackFileName
	 *            the fallback filename if the online resource does not provide
	 *            its filename
	 * @param l
	 *            can be null, if no error or update information is needed
	 * @return the downloaded {@link File} or null if an error happened
	 */
	public static File loadFileFromUri(URL sourceUri, File targetFolder,
			String fallbackFileName, FileFromUriListener l) {
		try {
			if (!targetFolder.exists() && !targetFolder.mkdirs()) {
				if (l != null) {
					l.onError(new Exception("Could not create folder "
							+ targetFolder));
				}
				return null;
			}
			Log.d(LOG_TAG, "Downoading " + sourceUri);
			URL url = sourceUri;
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

			// debugOutputHeaderFields(connection);

			Integer fileSizeOnServer = null;
			try {
				String fileSizeString = connection
						.getHeaderField("Content-Length");
				if (fileSizeString != null) {
					fileSizeOnServer = Integer.parseInt(fileSizeString);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			String rawContDisp = connection
					.getHeaderField("Content-Disposition");
			String downloadFileName = null;
			if (rawContDisp != null) {
				Log.d("raw=" + rawContDisp);
				Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
				Matcher regexMatcher = regex.matcher(rawContDisp);
				if (regexMatcher.find()) {
					downloadFileName = regexMatcher.group();
				} else if (rawContDisp.contains("=")) {
					downloadFileName = ""
							+ rawContDisp.subSequence(
									rawContDisp.lastIndexOf("=") + 1,
									rawContDisp.length());
					downloadFileName = downloadFileName.trim();
				}
			}
			if (downloadFileName == null || downloadFileName.isEmpty()) {
				downloadFileName = fallbackFileName;
			}
			if (fallbackFileName != null && !fallbackFileName.isEmpty()
					&& !downloadFileName.contains(".")) {
				downloadFileName = fallbackFileName;
			}

			InputStream input = connection.getInputStream();
			if (l != null) {
				long lastModifiedDate = 0;
				try {
					String lmString = connection
							.getHeaderField("Last-Modified");
					if (lmString != null) {
						lastModifiedDate = Long.parseLong(lmString);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (lastModifiedDate <= 0) {
					Log.d(LOG_TAG, "lastModifiedDate=" + lastModifiedDate
							+ ", trying to get it from getLastModified()");
					lastModifiedDate = connection.getLastModified();
				}
				if (lastModifiedDate <= 0) {
					Log.d(LOG_TAG, "lastModifiedDate=" + lastModifiedDate
							+ ", trying to get it from header field 'Date'");
					lastModifiedDate = connection.getHeaderFieldDate("Date", 0);
				}
				// debugOutputHeaderFields(connection);
				Log.v(LOG_TAG, "final lastModifiedDate=" + lastModifiedDate);
				File cachedFile = l.onGetCachedVersion(fallbackFileName,
						lastModifiedDate, fileSizeOnServer);
				if (cachedFile != null) {
					return cachedFile; // abort download
				}
			}
			File targetFile = new File(targetFolder, downloadFileName);
			OutputStream output = new FileOutputStream(targetFile);

			byte data[] = new byte[4096];
			long total = 0;
			int count;

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

	private static void debugOutputHeaderFields(HttpURLConnection connection) {
		Map<String, List<String>> f = connection.getHeaderFields();
		for (Entry<String, List<String>> e : f.entrySet()) {
			Log.i(LOG_TAG, "       > header key=" + e.getKey());
			List<String> values = e.getValue();
			for (String v : values) {
				Log.i(LOG_TAG, "              >> value=" + v);
			}
		}
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

	public static boolean deleteFolder(File folderToDelete) {
		if (folderToDelete.isDirectory()) {
			String[] children = folderToDelete.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFolder(new File(folderToDelete,
						children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return folderToDelete.delete(); // The directory is empty now and can be
										// deleted.
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

	public static void saveStringToExternalStorage(String filename,
			String textToSave) throws IOException {
		File file = newFile(filename);
		FileOutputStream foStream = new FileOutputStream(file);
		OutputStreamWriter stringOut = new OutputStreamWriter(foStream);
		stringOut.write(textToSave);
		stringOut.close();
		foStream.close();
	}

	public static boolean saveStringToFile(File targetFile, String stringToSave)
			throws IOException {
		targetFile = newFile(targetFile);
		PrintWriter out = new PrintWriter(targetFile);
		out.println(stringToSave);
		out.flush();
		out.close();
		return true;
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

	/**
	 * Creates the file plus all needed folders
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
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
			copyFromInToOut(in, out);
			out.close();
		}
	}

	private static void copyFromInToOut(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		in.close();
	}

	public static boolean zip(File targetFile, boolean overrideExistingFile,
			File... filesToZip) {
		if (!overrideExistingFile && targetFile.exists()) {
			return false;
		}
		try {
			newFile(targetFile);
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					targetFile));
			for (File f : filesToZip) {
				out.putNextEntry(new ZipEntry(f.getName()));
				copyFromInToOut(new FileInputStream(f), out);
				out.closeEntry();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void unzip(ZipFile sourceZipFile, File targetFile) {
		try {
			targetFile.mkdir();
			Enumeration entries = sourceZipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory()) {
					(new File(targetFile, entry.getName())).mkdirs();
					continue;
				}
				File entryFile = new File(targetFile, entry.getName());
				IOHelper.newFile(entryFile);
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(entryFile));
				copyFromInToOut(sourceZipFile.getInputStream(entry), out);
				out.close();
			}
			sourceZipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static byte[] loadFileAsByteArray(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		is.close();
		return buffer.toByteArray();
	}

	public static List<File> getFilesInPath(String path) {
		return getFilesInPath(new File(path));
	}

	public static List<File> getFilesInPath(File directory) {
		File[] files = directory.listFiles();
		if (files == null || files.length == 0) {
			return new ArrayList<File>();
		}
		return Arrays.asList(files);
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
