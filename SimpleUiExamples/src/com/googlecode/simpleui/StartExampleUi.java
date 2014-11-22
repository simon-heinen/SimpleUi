package com.googlecode.simpleui;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_Caption;
import v2.simpleUi.M_Checkbox;
import v2.simpleUi.M_Container;
import v2.simpleUi.M_InfoText;
import android.R;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

public class StartExampleUi extends M_Container {

	public StartExampleUi() {

		final Context myMainActivity = getContext();

		// context can be your main activity e.g.:
		final Context context = myMainActivity;
		final M_Container box = this;// new M_Container();
		box.add(new M_Caption("Hello World"));
		box.add(new M_InfoText(R.drawable.ic_dialog_info,
				"This is an example UI with 4 elements to demonstrate "
						+ "how the SimpleUI concepts can be applied."));
		box.add(new M_Checkbox() {
			@Override
			public CharSequence getVarName() {
				return "I understand!";
			}

			@Override
			public boolean loadVar() {
				// when the UI is shown the checkbox should not be checked:
				boolean initialCheckboxValue = false;
				return initialCheckboxValue;
			}

			@Override
			public boolean save(boolean newCheckboxValue) {
				// e.g update your model: myModel.setValueXYZ(newCheckboxValue);
				// then return true to signalize that the new value was accepted
				// in this case the user has to check the checkbox to continue:
				if (!newCheckboxValue) {
					Toast.makeText(context, "You need to understand ",
							Toast.LENGTH_LONG).show();
				}
				return (newCheckboxValue == true);
			}
		});
		box.add(new M_Button("Save") {
			@Override
			public void onClick(Context c, Button b) {
				// trigger the update of the model (will call all save methods):
				box.save();
			}
		});

		// the following is now already done in the main activity:
		// // To display the generated UI the SimpleUI class can be used:
		// SimpleUI.showUi(context, box);
		// // or you generate the UI for this box controller:
		// View generatedView = box.getView(context);
	}
}
