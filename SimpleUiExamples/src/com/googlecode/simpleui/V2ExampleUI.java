package com.googlecode.simpleui;

import java.util.ArrayList;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_CancelSave;
import v2.simpleUi.M_Caption;
import v2.simpleUi.M_Container;
import v2.simpleUi.M_ImageGallery;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.M_TextInput;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.SimpleUI;
import v2.simpleUi.uiDecoration.ExampleDecorator;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

/**
 * This example activity does not use the the {@link SimpleUI} activity and thus
 * does not store any activity state when any property changes (read
 * http://stackoverflow
 * .com/questions/151777/how-do-i-save-an-android-applications-state for more
 * infos). You would have to implement it yourself if you would use your own
 * activity (like this one here) instead of the {@link SimpleUI} activity.
 * 
 * @author Simon Heinen
 * 
 */
public class V2ExampleUI extends Activity {

	private static String myValue;
	private static boolean myBool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		M_Container modifierList = new M_Container();

		modifierList.add(new M_Caption("Test ABC 123 @@@"));

		modifierList.add(new M_InfoText(R.drawable.arrow_down_float,
				"This is the new v2 version of simple UI"));
		modifierList.add(new M_InfoText(R.drawable.arrow_down_float, "It can "
				+ "be used in a similar" + " way to v1 and is compatible with "
				+ "all the existing v1 modifiers."));
		modifierList.add(new M_InfoText(R.drawable.arrow_down_float,
				"The new modifiers are designed to "
						+ "be integrated into any UI. Just call "
						+ "getView() on any modifier to use "
						+ "it without the SimpleUI activity eg"));

		modifierList.add(new M_TextInput() {

			@Override
			public boolean save(String string) {
				myValue = string;
				return true;
			}

			@Override
			public String load() {
				return myValue;
			}

			@Override
			public String getVarName() {
				return "Test";
			}
		});

		modifierList.add(new v2.simpleUi.M_Checkbox() {

			@Override
			public boolean loadVar() {

				return myBool;
			}

			@Override
			public CharSequence getVarName() {
				return "My Bool";
			}

			@Override
			public boolean save(boolean newValue) {
				return myBool = newValue;
			}

		});

		modifierList.add(new M_ImageGallery() {

			@Override
			public boolean save(int selectedItemId) {
				return true;
			}

			@Override
			public String getVarName() {
				return "Images";
			}

			@Override
			public ArrayList<Integer> getImageIds() {
				ArrayList<Integer> a = new ArrayList<Integer>();
				a.add(R.drawable.arrow_up_float);
				a.add(R.drawable.btn_radio);
				a.add(R.drawable.dialog_frame);
				a.add(R.drawable.ic_dialog_info);
				a.add(R.drawable.ic_lock_idle_low_battery);
				a.add(R.drawable.ic_menu_my_calendar);
				a.add(R.drawable.ic_menu_upload);
				return a;
			}
		});

		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(newMod());
		modifierList.add(new M_CancelSave("Cancel", "Save", modifierList));

		modifierList.assignNewDecorator(new ExampleDecorator());

		setContentView(modifierList.getView(this));

	}

	private ModifierInterface newMod() {
		return new M_Button("Nothing here") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				// TODO Auto-generated method stub

			}
		};
	}
}
