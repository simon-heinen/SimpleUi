package simpleui.examples.activities;

import simpleui.SimpleUI;
import simpleui.modifiers.v3.M_CancelSave;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_TextModifier;
import simpleui.modifiers.v3.M_Toolbar;
import android.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * This example activity does not use the the {@link SimpleUI} activity and thus
 * does not store any activity state when any property changes (read
 * http://stackoverflow
 * .com/questions/151777/how-do-i-save-an-android-applications-state for more
 * infos). You would have to implement it yourself if you would use your own
 * activity (like this one here) instead of the {@link SimpleUI} activity.
 */
public class ExampleActivity2 extends Activity {

	private String myStringValue;
	private boolean myBoolValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		M_Container modifierList = new M_Container();

		modifierList.add(new M_Toolbar("ExampleActivity2"));

		modifierList.add(new M_InfoText(R.drawable.ic_dialog_info,
				"This UI is generated using SimpleUI modifiers"));
		modifierList.add(new M_InfoText("The modifiers are designed to "
				+ "be integrated into any UI. Just call "
				+ "getView() on any modifier to use "
				+ "it without the SimpleUI activity e.g."));

		modifierList.add(new M_TextModifier() {

			@Override
			public boolean save(String newValue) {
				myStringValue = newValue;
				return true;
			}

			@Override
			public String load() {
				return myStringValue;
			}

			@Override
			public String getVarName() {
				return "I'm a M_TextModifier";
			}
		});

		modifierList.add(new simpleui.modifiers.v3.M_Checkbox() {

			@Override
			public boolean loadVar() {

				return myBoolValue;
			}

			@Override
			public CharSequence getVarName() {
				return "I'm a M_Checkbox";
			}

			@Override
			public boolean save(boolean newValue) {
				return myBoolValue = newValue;
			}

		});

		modifierList.add(new M_CancelSave("Cancel", "Save", modifierList));

		setContentView(modifierList.getView(this));

	}

}
