package simpleui.examples;

import java.util.ArrayList;
import java.util.List;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_CardView;
import simpleui.modifiers.v3.M_Checkbox;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_FloatModifier;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_IntModifier;
import simpleui.modifiers.v3.M_ProgressBar;
import simpleui.modifiers.v3.M_RadioButtonList;
import simpleui.modifiers.v3.M_RadioButtonList.DefaultSelectableItem;
import simpleui.modifiers.v3.M_SeperatorLine;
import simpleui.modifiers.v3.M_Slider;
import simpleui.modifiers.v3.M_TextModifier;
import android.R;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

/**
 * The example card methods are all static to make sure they have no
 * dependencies to the {@link M_ModifierOverview} class where they are located
 * in
 */
public class M_ModifierOverview extends M_Container {

	public M_ModifierOverview() {
		setCardBackgroundColor(Color.DKGRAY);
		add(newM_CardView());
		add(newM_ButtonExample());
		add(newM_CheckboxAndM_RadioButtonList());
		add(newM_TextModifierExamples());
		add(newM_ProgressBarExample());

	}

	private static void toast(Context c, String text) {
		Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
	}

	private static M_CardView newM_CardView() {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("This is a M_CardView"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_InfoText(R.drawable.ic_dialog_info,
				"It can contain as many modifiers as you want."));
		c.add(new M_InfoText("Modifiers will be grouped in separate "
				+ "cards to give you a better overview."));
		return c;
	}

	private static M_CardView newM_ButtonExample() {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("M_Button examples"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_Button("Button A") {

			@Override
			public void onClick(Context context, Button arg1) {
				toast(context, "I'm Button A");
			}
		});
		c.add(new M_Button("Borderless Button B", true) {

			@Override
			public void onClick(Context context, Button arg1) {
				toast(context, "I'm a borderless button");
			}
		});
		return c;
	}

	private static M_CardView newM_CheckboxAndM_RadioButtonList() {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("M_RadioButtonList"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_Checkbox() {
			private boolean exampleBool = true;

			@Override
			public CharSequence getVarName() {
				return "I'm a M_Checkbox";
			}

			@Override
			public boolean loadVar() {
				return exampleBool;
			}

			@Override
			public boolean save(boolean checkboxChecked) {
				exampleBool = checkboxChecked;
				return true;
			}

		});
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_InfoText("M_RadioButtonList:"));
		c.add(new M_RadioButtonList<DefaultSelectableItem>() {

			private Integer selectedItemId = 2;

			@Override
			public Integer loadSelectedItemId() {
				return selectedItemId;
			}

			@Override
			public boolean save(DefaultSelectableItem i) {
				selectedItemId = i.getId();
				return true;
			}

			@Override
			public List<DefaultSelectableItem> getItemList() {
				List<DefaultSelectableItem> l = new ArrayList<DefaultSelectableItem>();
				l.add(new DefaultSelectableItem(1, "M_RadioButtonList Item 1"));
				l.add(new DefaultSelectableItem(2, "M_RadioButtonList Item 2"));
				return l;
			}

		});
		return c;
	}

	private static ModifierInterface newM_TextModifierExamples() {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("These are some M_TextModifier examples"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_TextModifier() {
			String textToModify = "Abc.. (an initial text)";

			@Override
			public String getVarName() {
				return "A default M_TextModifier";
			}

			@Override
			public String load() {
				return textToModify;
			}

			@Override
			public boolean save(String updatedText) {
				textToModify = updatedText;
				return true;
			}

		});
		c.add(new M_TextModifier(true, true, false) {
			String textToModify = "Abc.. (an initial text)";

			@Override
			public String getVarName() {
				return "One for lots of text:";
			}

			@Override
			public String load() {
				return textToModify;
			}

			@Override
			public boolean save(String updatedText) {
				textToModify = updatedText;
				return true;
			}

		});
		c.add(new M_IntModifier() {
			int valueToModify = 42;

			@Override
			public String getVarName() {
				return "An M_IntModifier:";
			}

			@Override
			public int loadInt() {
				return valueToModify;
			}

			@Override
			public boolean saveInt(int newValue) {
				valueToModify = newValue;
				return true;
			}

		});
		c.add(new M_FloatModifier() {
			float valueToModify = 42.42f;

			@Override
			public String getVarName() {
				return "An M_IntModifier:";
			}

			@Override
			public float loadFloat() {
				return valueToModify;
			}

			@Override
			public boolean saveFloat(float newValue) {
				valueToModify = newValue;
				return true;
			}

		});
		c.add(new M_InfoText("Additionally there is an "
				+ "M_DoubleModifier and an M_LongModifier"));
		return c;
	}

	private static ModifierInterface newM_ProgressBarExample() {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("M_ProgressBar and M_Slider"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_InfoText("An M_ProgressBar (which can be "
				+ "updated by M_Slider):"));
		final int maxValue = 100;
		final M_ProgressBar progressbar = new M_ProgressBar() {

			@Override
			public int loadInitValue() {
				return 50;
			}

			@Override
			public int loadMaxValue() {
				return maxValue;
			}
		};
		c.add(progressbar);
		c.add(new M_InfoText("An M_Slider:"));
		c.add(new M_Slider(maxValue) {
			private int valueToModify = 20;

			@Override
			public int loadCurrentValue() {
				return valueToModify;
			}

			@Override
			public void onProgressUpdate(int progressValue,
					boolean userHasFingerStillDown) {
				progressbar.setValue(progressValue);
			}

			@Override
			public boolean save(int newValue) {
				valueToModify = newValue;
				return true;
			}
		});
		return c;
	}

}
