package simpleui.examples.injection;

public class TestModelClassForDaggerTests {

	/**
	 * the returned string will be overwritten by the injected subclass of
	 * {@link TestModelClassForDaggerTests} which is created in the {@link DaggerInjector}
	 * factory
	 */
	public String getMyText() {
		return "A";
	}

}
