package util;

import java.lang.reflect.Modifier;
import java.util.List;

public abstract class ClassFinder {

	public interface ResultListener<T> {
		void runTestsFor(T c);

		void onError(Exception e);
	}

	private static final String LOG_TAG = "ClassFinder";

	public static <T> void runInPackage(String packageName,
			Class<T> searchedClassType, ResultListener<T> result) {
		try {
			List<Class> l = PackageSearcher
					.getAllClassesAndInnerClassesIn(packageName);
			// System.out.println("Running test for " + l.size() +
			// " classes in " + packageName);
			runForFoundClasses(l, searchedClassType, result);
		} catch (Exception e) {
			result.onError(e);
		}
	}

	private static <T> void runForFoundClasses(List<Class> list,
			Class<T> searchedClassType, ResultListener<T> result) {
		for (Class c : list) {
			if (!Modifier.isAbstract(c.getModifiers())
					&& !Modifier.isInterface(c.getModifiers())) {
				if (searchedClassType.isAssignableFrom(c)) {
					try {
						result.runTestsFor(((Class<T>) c).newInstance());
					} catch (InstantiationException e) {
						Log.e(LOG_TAG, "Skipping " + c
								+ " because default empty constructor missing");
						result.onError(e);
					} catch (IllegalAccessException e) {
						result.onError(e);
					}
				}
			}
		}
	}

}
