package injectionTests;

import javax.inject.Inject;

import tools.ButterknifeHelper;
import v2.simpleUi.SimpleUiApplication;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.InjectView;
import dagger.ObjectGraph;
import de.rwth.R;

/**
 * see http://jakewharton.github.io/butterknife/ for details
 * 
 * in eclipse the following additional steps are needed to use the Butterknife
 * library http://jakewharton.github.io/butterknife/ide-eclipse.html
 * 
 */
public class ButterknifeAndDaggerTestActivity extends Activity {

	// dagger
	@Inject
	TestModelClassForDaggerTests injectedByDagger;

	// butterknife
	@InjectView(R.id.title)
	TextView title;

	@InjectView(R.id.secondTitle)
	TextView secondTitle;

	@InjectView(R.id.text1)
	TextView text1;

	@InjectView(de.rwth.R.id.text2)
	TextView text2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterknifeHelper.load(this, R.layout.butterknife_test_ui);
		title.setText("Hallo");
		secondTitle.setText("B");
		text1.setText("aaaaaa");

		// call this one time at the beginning of your main activity:
		((SimpleUiApplication) getApplication())
				.getObjectGraph(new DaggerInjector());

		ObjectGraph o = ((SimpleUiApplication) getApplication())
				.getObjectGraph();
		o.inject(this);

		text2.setText("dagger(should not be A)=" + injectedByDagger.getMyText());
	}

}
