package simpleui.examples;

import simpleui.SimpleUI;
import simpleui.examples.activities.ExampleActivity1;
import simpleui.examples.listwrapper.M_ListWrapperV3Tests;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_Collection;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_SeperatorLine;
import simpleui.modifiers.v3.M_Toolbar;
import simpleui.util.ErrorHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LatLng x = new LatLng(2d, 2d);
		simpleui.util.Log.d("", "x=" + x);

		registerErrorHandlerToCatchExceptions();

		M_Container container = new M_Container();
		container.add(new M_Toolbar("Simple UI Examples Overview"));
		container
				.add(new M_InfoText(
						"The following examples will give you a general overview about the concepts developed in SimpleUI, use this example-app in combination with the sourcecode to get most out of this."));

		addButtonsForBasicDemos(container);
		addButtonsForOtherDemosAndTests(container);

		setContentView(container.getView(this));
	}

	private void registerErrorHandlerToCatchExceptions() {
		ErrorHandler.registerNewErrorHandler(this,
				"errors/testErrorHandlerSimpleUiTests");
		ErrorHandler.enableEmailReports("simon.heinen@gmail.com",
				"Error in SimpleUi Test project");
	}

	private void addButtonsForBasicDemos(M_Container c) {
		M_Container innerContainer = new M_Container();
		c.add(innerContainer);
		innerContainer.add(new M_Caption("Basic examples"));
		innerContainer.add(M_SeperatorLine.newMaterialOne());
		innerContainer.add(new M_Button("Start M_ModifierOverview "
				+ "(a short intro to all standard modifiers)") {

			private final M_ModifierOverview m_ModifierOverview = new M_ModifierOverview();

			@Override
			public void onClick(Context context, Button b) {
				SimpleUI.showInfoDialog(context, "Close", m_ModifierOverview);
			}
		});
		innerContainer.add(new M_Button("Show M_ExampleDemoUiV1") {
			@Override
			public void onClick(Context context, Button clickedButton) {
				ModifierInterface box = new M_ExampleDemoUiV1();
				SimpleUI.showUi(context, box);
			}
		});
	}

	private void addButtonsForOtherDemosAndTests(M_Container c) {
		M_Container innerContainer = new M_Container();
		c.add(innerContainer);
		innerContainer.add(new M_Caption("Other demos"));

		innerContainer.add(M_SeperatorLine.newMaterialOne());
		addButtonToStartModifier(innerContainer,
				"A container to test all latest features added to SimpleUI",
				new M_CurrentNewFeatureTests());
		addButtonToStartModifier(innerContainer, "Some Material UI tests",
				new M_CardViewTests());

		innerContainer.add(new M_Button(
				"Some examples with different ListViews") {
			@Override
			public void onClick(Context arg0, Button arg1) {
				M_Container c = new M_Container();
				c.add(M_ListWrapperV3Tests.newButtonForM_ListWrapperV4Tests());
				c.add(M_ListWrapperV3Tests.newButtonForM_ListWrapperV3Grid());
				c.add(M_ListWrapperV3Tests.newButtonForM_ListWrapperV3Tests());
				SimpleUI.showInfoDialog(arg0, "Close", c);
			}
		});
		innerContainer
				.add(new M_Button(
						"Start ExampleActivity1 (An older collection of many modifier tests)") {

					@Override
					public void onClick(Context context, Button b) {
						startActivity(new Intent(MainActivity.this,
								ExampleActivity1.class));
					}
				});
	}

	private void addButtonToStartModifier(M_Container targetContainer,
			String nameOfModifier, final M_Collection modifierToShow) {
		targetContainer.add(new M_Button(nameOfModifier) {
			@Override
			public void onClick(Context context, Button clickedButton) {
				try {
					SimpleUI.showInfoDialog(context, "Close", modifierToShow);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
