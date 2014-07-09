package util;

import junit.framework.TestCase;
import util.ClassFinder.ResultListener;

public class ClassFinderTests extends TestCase {

	public void testA() {

		ResultListener<TestClass> l = new ClassFinder.ResultListener<TestClass>() {

			@Override
			public void runTestsFor(TestClass c) {
				System.out.println("new instance created for " + c);
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}

			@Override
			public void onFinished() {
			}
		};

		// to start the search use:
		ClassFinder.runInSamePackageAs(TestClass.class, TestClass.class, l);
		// or
		ClassFinder.runInPackage("util", TestClass.class, l);
	}

}
