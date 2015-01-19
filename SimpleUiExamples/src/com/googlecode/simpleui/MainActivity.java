package com.googlecode.simpleui;

import simpleui.SimpleUI;
import simpleui.examples.M_CardViewTests;
import simpleui.examples.M_ExampleDemoUiV1;
import simpleui.examples.M_ModifierOverview;
import simpleui.examples.activities.ExampleActivity1;
import simpleui.examples.listwrapper.M_ListWrapperV3Tests;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_SeperatorLine;
import simpleui.modifiers.v3.M_Toolbar;
import simpleui.util.ErrorHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ErrorHandler.registerNewErrorHandler(this,
				"errors/testErrorHandlerSimpleUiTests");
		ErrorHandler.enableEmailReports("simon.heinen@gmail.com",
				"Error in SimpleUi Test project");

		M_Container c = new M_Container();

		c.add(new M_Toolbar("Simple UI Examples Overview"));

		addButtonsForBasicDemos(c);
		addButtonsForOtherDemosAndTests(c);

		setContentView(c.getView(this));

	}

	private void addButtonsForBasicDemos(M_Container c) {
		M_Container innerContainer = new M_Container();
		c.add(innerContainer);
		innerContainer.add(new M_Caption("Basic examples"));
		innerContainer.add(M_SeperatorLine.newMaterialOne());
		innerContainer.add(new M_Button("Start M_ModifierOverview "
				+ "(a short intro to all standard modifiers)") {
			M_ModifierOverview m_ModifierOverview = new M_ModifierOverview();

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
		innerContainer.add(new M_Button("Some Material UI tests") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				try {
					SimpleUI.showInfoDialog(context, "Close",
							new M_CardViewTests());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		innerContainer.add(new M_Button("List examples") {

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
						"Start ExampleActivity1 (A collection of many modifier tests)") {

					@Override
					public void onClick(Context context, Button b) {
						startActivity(new Intent(MainActivity.this,
								ExampleActivity1.class));
					}
				});
	}

}
