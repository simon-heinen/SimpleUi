package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

public abstract class ClassFinder {

	public interface ResultListener<T> {
		void runTestsFor(T c);

		void onError(Exception e);

		void onFinished();
	}

	private static final String LOG_TAG = "ClassFinder";

	/**
	 * @param obj
	 *            will use the
	 * @param searchedClassType
	 * @param result
	 */
	public static <T> void runInSamePackageAs(Class c,
			Class<T> searchedClassType, ResultListener<T> result) {
		runInPackage(c.getPackage().getName(), searchedClassType, result);
	}

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
						createInstanceFromPrivateConstructor(c, result);
					} catch (Exception e) {
						result.onError(e);
					}
				}
			}
		}
		result.onFinished();
	}

	private static <T> void createInstanceFromPrivateConstructor(Class c,
			ResultListener<T> result) {
		Log.i(LOG_TAG, "Class " + c + " did not have a public default "
				+ "constructor, will use the private one");
		try {
			Constructor<T> ccc = ((Class<T>) c).getDeclaredConstructor();
			ccc.setAccessible(true);
			result.runTestsFor(ccc.newInstance());
		} catch (Exception e2) {
			result.onError(e2);
		}
	}

}
