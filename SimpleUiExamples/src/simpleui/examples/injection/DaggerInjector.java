package simpleui.examples.injection;

import dagger.Module;
import dagger.Provides;

/**
 * this example injection modules keeps the information what concrete subclasses
 * are generated when a object instance is injected.
 */
@Module(injects = { ExampleButterknifeAndDaggerActivity.class })
public class DaggerInjector {

	/**
	 * It does not matter what the method name is only the @provides annotation
	 * and the return type are the important factors
	 * 
	 */
	@Provides
	TestModelClassForDaggerTests a() {
		return new TestModelClassForDaggerTests() {
			@Override
			public String getMyText() {
				return "B";
			}
		};
	}

}
