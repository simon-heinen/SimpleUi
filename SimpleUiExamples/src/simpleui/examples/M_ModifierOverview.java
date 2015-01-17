package simpleui.examples;

import java.util.ArrayList;
import java.util.List;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_Checkbox;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_Container2;
import simpleui.modifiers.v3.M_FloatModifier;
import simpleui.modifiers.v3.M_HalfHalf;
import simpleui.modifiers.v3.M_ImageView;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_IntModifier;
import simpleui.modifiers.v3.M_ProgressBar;
import simpleui.modifiers.v3.M_RadioButtonList;
import simpleui.modifiers.v3.M_RadioButtonList.DefaultSelectableItem;
import simpleui.modifiers.v3.M_SeperatorLine;
import simpleui.modifiers.v3.M_Slider;
import simpleui.modifiers.v3.M_Spinner;
import simpleui.modifiers.v3.M_Switch;
import simpleui.modifiers.v3.M_TextModifier;
import simpleui.modifiers.v3.M_Toolbar;
import simpleui.modifiers.v3.M_WebView;
import simpleui.util.MenuItemList;
import simpleui.util.MenuItemList.MItem;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
		addM_ToolbarExample();
		add(newM_CardViewAndM_InfoTextExamples());
		add(newM_ButtonExamples());
		add(newM_HalfHalfExamples());
		add(newM_ImageViewExample());
		add(newM_CheckboxAndM_RadioButtonListExamples());
		add(newM_TextModifierExamples());
		add(newM_ProgressBarExamples());
		add(newM_SpinnerExample());
		add(newM_WebviewExample());
		add(newM_Container2Example());
	}

	private static void showToast(Context c, String text) {
		Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
	}

	private void addM_ToolbarExample() {
		add(new M_Toolbar("Modifier Overview"));
		MenuItemList menuItemList = new MenuItemList();
		menuItemList.add(new MItem("I'm an M_Toolbar, click here", null) {
			@Override
			public void onClick(Activity context) {
				showToast(context, "Item in M_Toolbar clicked");
			}
		});
		setMenuItemList(menuItemList);
	}

	private static M_Container newM_CardViewAndM_InfoTextExamples() {
		M_Container c = new M_Container();
		c.add(new M_Caption("This is a M_CardView"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_InfoText("Modifiers will be grouped in separate "
				+ "cards to give you a better overview."));
		c.add(new M_InfoText(R.drawable.ic_dialog_info,
				"A M_CardView can contain as many modifiers as you want."));
		c.add(new M_InfoText(
				"This information is shown using M_InfoText modifiers. These modifiers display normal unmodifiable content."));

		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_InfoText("Example caption:",
				"M_InfoText can also be used for such structured text."));
		c.add(new M_InfoText("Another caption:", "Another example description"));
		c.add(M_SeperatorLine.newMaterialOne(null));

		M_InfoText infoTextWithUrlsEnabled = new M_InfoText(
				R.drawable.ic_dialog_alert,
				"Text blocks with embedded URLs like google.com are supported as well!");
		infoTextWithUrlsEnabled.setContainsUrls(true);
		c.add(infoTextWithUrlsEnabled);
		return c;
	}

	private static M_Container newM_ButtonExamples() {
		M_Container c = new M_Container();
		c.add(new M_Caption("M_Button examples"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_Button("Button A") {

			@Override
			public void onClick(Context context, Button arg1) {
				showToast(context, "I'm Button A");
			}
		});
		c.add(new M_Button("Borderless Button B", true) {

			@Override
			public void onClick(Context context, Button arg1) {
				showToast(context, "I'm a borderless button");
			}
		});
		return c;
	}

	private static M_Container newM_HalfHalfExamples() {
		M_Container c = new M_Container();
		c.add(new M_Caption("M_HalfHalf examples"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_InfoText("A first M_HalfHalf example with 2 buttons:"));
		ModifierInterface left = new M_Button("Left button in M_HalfHalf") {

			@Override
			public void onClick(Context context, Button arg1) {
				showToast(context, "I'm the left button");
			}
		};
		ModifierInterface right = new M_Button("Right button in M_HalfHalf") {

			@Override
			public void onClick(Context context, Button arg1) {
				showToast(context, "I'm the right button");
			}
		};
		c.add(new M_HalfHalf(left, right));
		c.add(M_SeperatorLine.newMaterialOne(null));
		left = new M_ImageView(Uri.parse("http://lorempixel.com/200/320/"));
		right = new M_InfoText(
				"Like in this second M_HalfHalf the 2 modifiers can have different weights, to make one modifier bigger than the other. In this case the left one is a M_ImageView");
		c.add(new M_HalfHalf(left, right, 2, 1));
		c.add(M_SeperatorLine.newMaterialOne(null));
		return c;
	}

	private static M_Container newM_ImageViewExample() {
		M_Container c = new M_Container();
		c.add(new M_ImageView(Uri.parse("http://lorempixel.com/500/300/"),
				"I'm another M_ImageView with a caption on it"));
		c.add(new M_InfoText(
				"An M_ImageView can have a caption and will always fill its given width like you can see in this example and the other one above."));
		c.add(new M_InfoText(
				"The design of this card tries to mimic an example card from the Android design guidelines to evaluate the usability of the Modifier principle."));
		c.add(M_SeperatorLine.newMaterialOne(null));

		ModifierInterface left = new M_Button("SHARE", true) {

			@Override
			public void onClick(Context context, Button arg1) {
				showToast(context, "SHARE clicked");
			}
		};
		ModifierInterface right = new M_Button("EXPLORE", true) {

			@Override
			public void onClick(Context context, Button arg1) {
				showToast(context, "EXPLORE clicked");
			}
		};
		c.add(M_HalfHalf.GoldenCutLeftLarge(new M_HalfHalf(left, right), null));
		return c;
	}

	private static M_Container newM_CheckboxAndM_RadioButtonListExamples() {
		M_Container c = new M_Container();
		c.add(new M_Caption("M_Checkbox & M_RadioButtonList"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_Checkbox("I'm an M_Checkbox") {
			private boolean exampleBool = true;

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
		c.add(new M_Switch("I'm an M_Switch") {
			private boolean exampleBool = true;

			@Override
			public boolean loadVar() {
				return exampleBool;
			}

			@Override
			public boolean save(boolean newValue) {
				exampleBool = newValue;
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
		M_Container c = new M_Container();
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
				return "An M_FloatModifier:";
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

	private static ModifierInterface newM_ProgressBarExamples() {
		M_Container c = new M_Container();
		c.add(new M_Caption("M_ProgressBar and M_Slider"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_InfoText("An M_ProgressBar (which is automatically "
				+ "updated by the M_Slider):"));
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
		c.add(new M_InfoText("I'm an M_Slider:"));
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

	private static M_Container newM_SpinnerExample() {
		M_Container c = new M_Container();
		c.add(new M_Caption("This is an M_Spinner example"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_Spinner() {
			private int selectedItemId = 2;

			@Override
			public String getVarName() {
				return "An M_Spinner";
			}

			@Override
			public int loadSelectedItemId() {
				return selectedItemId;
			}

			@Override
			public List<SpinnerItem> loadListToDisplay() {
				List<SpinnerItem> l = new ArrayList<M_Spinner.SpinnerItem>();
				l.add(new SpinnerItem(1, "Spinner Item 1"));
				l.add(new SpinnerItem(2, "Spinner Item 2"));
				l.add(new SpinnerItem(3, "Spinner Item 3"));
				l.add(new SpinnerItem(4, "Spinner Item 4"));
				return l;
			}

			@Override
			public boolean save(SpinnerItem selectedItem) {
				selectedItemId = selectedItem.getId();
				return true;
			}

		});
		return c;
	}

	private static M_Container newM_WebviewExample() {
		M_Container c = new M_Container();
		c.add(new M_Caption("M_WebView example:"));
		c.add(M_SeperatorLine.newMaterialOne(null));
		c.add(new M_WebView(true, false) {
			@Override
			public String getUrlToDisplay() {
				return "https://www.google.com";
			}

			@Override
			public void onPageLoadProgress(int percent) {
			}

			@Override
			protected void onPageLoaded(String url) {
			}

		});
		return c;
	}

	private static M_Container newM_Container2Example() {
		M_Container card = new M_Container();
		M_Container2 c = new M_Container2(
				"I'm an M_Container2: Click here to collapse me");
		card.add(c);
		c.add(M_SeperatorLine.newMaterialOne(null));
		for (int i = 0; i < 10; i++) {
			c.add(new M_InfoText("Example text " + i));
		}
		return card;
	}

}
