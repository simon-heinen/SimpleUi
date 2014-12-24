package simpleui.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * The code below gets all classes within a given package
 * 
 * @author http://snippets.dzone.com/posts/show/4831
 * 
 */
@SuppressWarnings({ "unused", "rawtypes" })
public class PackageSearcher {
	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws Exception
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */

	public static List<Class> getAllClassesAndInnerClassesIn(String packageName)
			throws Exception {
		List<Class> result = new ArrayList<Class>();

		try {
			List<Class> classList = PackageSearcher.getClasses(packageName);
			if (classList == null || classList.size() == 0) {
				throw new Exception("The package '" + packageName
						+ "' was empty or did not exist");
			}
			result.addAll(classList);
			for (Class c : classList) {
				result.addAll(getAllSubclasses(c));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static List<Class> getAllSubclasses(Class c) {
		List<Class> result = new ArrayList<Class>();
		List<Class> classList = Arrays.asList(c.getDeclaredClasses());
		for (Class innerClass : classList) {
			result.addAll(getAllSubclasses(innerClass));
		}
		return classList;
	}

	private static List<Class> getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			String fileName = resource.getFile();
			String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
			dirs.add(new File(fileNameDecoded));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */

	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (file.isDirectory()) {
				assert !fileName.contains(".");
				classes.addAll(findClasses(file, packageName + "." + fileName));
			} else if (fileName.endsWith(".class") && !fileName.contains("$")) {
				Class _class;
				try {
					_class = Class.forName(packageName + '.'
							+ fileName.substring(0, fileName.length() - 6));
				} catch (ExceptionInInitializerError e) {
					// happen, for example, in classes, which depend on
					// Spring to inject some beans, and which fail,
					// if dependency is not fulfilled
					_class = Class.forName(
							packageName
									+ '.'
									+ fileName.substring(0,
											fileName.length() - 6), false,
							Thread.currentThread().getContextClassLoader());
				}
				classes.add(_class);
			}
		}
		return classes;
	}
}
