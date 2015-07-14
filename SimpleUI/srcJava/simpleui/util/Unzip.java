package simpleui.util;

import java.io.File;
import java.util.zip.ZipFile;

@Deprecated
public class Unzip {

	/**
	 * use {@link IOHelper#unzip(ZipFile, File)} instead
	 * 
	 * @param sourceZipFile
	 * @param targetFile
	 */
	@Deprecated
	public static void extract(ZipFile sourceZipFile, File targetFile) {
		IOHelper.unzip(sourceZipFile, targetFile);
	}
}
