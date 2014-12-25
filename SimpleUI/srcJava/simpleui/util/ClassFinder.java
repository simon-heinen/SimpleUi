package simpleui.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

public abstract class ClassFinder {

	/**
	 * Either a {@link ResultListener} or a {@link ClassResultListener}.
	 */
	private abstract interface GeneralResultListener<T> {
		void onError(Exception e);

		void onFinished();
	}

	/**
	 * Implement this interface if you want the {@link ClassFinder} to try to
	 * instantiate found classes for you (via Reflections, empty constructor
	 * needed).
	 */
	public interface ResultListener<T> extends GeneralResultListener<T> {
		void runTestsFor(T c);
	}

	/**
	 * Implement this interface if you want to process found classes as
	 * {@link Class} objects.
	 */
	public interface ClassResultListener<T> extends GeneralResultListener<T> {
		void runTestsFor(Class<? extends T> clazz);
	}

	private static final String LOG_TAG = ClassFinder.class.getSimpleName();

	/**
	 * @param obj
	 *            will use the
	 * @param searchedClassType
	 * @param result
	 *            either a {@link ResultListener} or a
	 *            {@link ClassResultListener}.
	 */
	public static <T> void runInSamePackageAs(final Class c,
			final Class<T> searchedClassType,
			final GeneralResultListener<T> result) {
		runInPackage(c.getPackage().getName(), searchedClassType, result);
	}

	public static <T> void runInPackage(final String packageName,
			final Class<T> searchedClassType,
			final GeneralResultListener<T> result) {
		try {
			final List<Class> l = PackageSearcher
					.getAllClassesAndInnerClassesIn(packageName);
			runForFoundClasses(l, searchedClassType, result);
		} catch (final Exception e) {
			result.onError(e);
		}
	}

	private static <T> void runForFoundClasses(final List<Class> list,
			final Class<T> searchedClassType,
			final GeneralResultListener<T> resultListener) {
		for (final Class c : list) {
			if (!Modifier.isAbstract(c.getModifiers())
					&& !Modifier.isInterface(c.getModifiers())) {
				if (searchedClassType.isAssignableFrom(c)) {
					if (resultListener instanceof ClassResultListener) {
						((ClassResultListener) resultListener).runTestsFor(c);
					} else if (resultListener instanceof ResultListener) {
						try {
							((ResultListener) resultListener)
									.runTestsFor(((Class<T>) c).newInstance());
						} catch (final InstantiationException e) {
							Log.e(LOG_TAG,
									"Skipping "
											+ c
											+ " because default empty constructor missing");
							resultListener.onError(e);
						} catch (final IllegalAccessException e) {
							createInstanceFromPrivateConstructor(c,
									(ResultListener) resultListener);
						} catch (final Exception e) {
							resultListener.onError(e);
						}
					} else {
						throw new RuntimeException(
								"Unknown Result Listener type");
					}
				}
			}
		}
		resultListener.onFinished();
	}

	private static <T> void createInstanceFromPrivateConstructor(final Class c,
			final ResultListener<T> result) {
		Log.i(LOG_TAG, "Class " + c + " did not have a public default "
				+ "constructor, will use the private one");
		try {
			final Constructor<T> ccc = ((Class<T>) c).getDeclaredConstructor();
			ccc.setAccessible(true);
			result.runTestsFor(ccc.newInstance());
		} catch (final Exception e2) {
			result.onError(e2);
		}
	}

}
