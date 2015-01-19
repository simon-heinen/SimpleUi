package simpleui.examples;

import simpleui.SimpleUI;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_Checkbox;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

/**
 * This is an example how to create and show a working UI: There are multiple
 * ways to show it:<br>
 * <br>
 * M_ExampleDemoUiV1 box = new M_ExampleDemoUiV1(); <br>
 * // To display the generated UI the {@link SimpleUI} class can be used:<br>
 * SimpleUI.showUi(context, box);<br>
 * // or you generate the UI for this box controller: <br>
 * View generatedView = box.getView(context);<br>
 * 
 */
public class M_ExampleDemoUiV1 extends M_Container {

	private static final long serialVersionUID = -1823400016600929569L;

	public M_ExampleDemoUiV1() {

		final Context myMainActivity = getContext();

		// context can be your main activity e.g.:
		final Context context = myMainActivity;
		final M_Container box = this;
		box.add(new M_Caption("Hello World"));
		box.add(new M_InfoText(R.drawable.ic_dialog_info,
				"This is an example UI with 4 elements to demonstrate "
						+ "how the SimpleUI concepts can be applied."));
		box.add(new M_Checkbox("I understand!") {

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
				boolean userCheckedCheckbox = newCheckboxValue;
				if (!userCheckedCheckbox) {
					Toast.makeText(context, "You need to understand ",
							Toast.LENGTH_LONG).show();
				}
				return userCheckedCheckbox;
			}
		});
		box.add(new M_Button("Save") {
			@Override
			public void onClick(Context c, Button b) {
				// trigger the update of the model (will call all save methods):
				if (box.save()) {
					// every modifier accepted the save request
					if (c instanceof Activity) {
						// close the window showing this container:
						((Activity) c).finish();
					}
				} else {
					// at least one modifier did not accept the save request (in
					// this example it must have been the M_Checkbox)
				}
			}
		});

	}
}
