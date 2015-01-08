package simpleui.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import simpleui.util.IOHelper;

public class Unzip {

	private static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}

		in.close();
		out.close();
	}

	public static void extract(ZipFile sourceZipFile, File targetFile) {
		try {
			targetFile.mkdir();
			Enumeration entries = sourceZipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory()) {
					// Assume directories are stored parents first then
					// children.
					// This is not robust, just for demonstration purposes.
					(new File(targetFile, entry.getName())).mkdir();
					continue;
				}
				File entryFile = new File(targetFile, entry.getName());
				IOHelper.newFile(entryFile);
				copyInputStream(sourceZipFile.getInputStream(entry),
						new BufferedOutputStream(
								new FileOutputStream(entryFile)));
			}
			sourceZipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
