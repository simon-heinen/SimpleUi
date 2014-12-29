package simpleui.examples;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_CardView;
import simpleui.modifiers.v3.M_Checkbox;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_HalfHalf;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_ProgressBar;
import simpleui.modifiers.v3.M_SeperatorLine;
import simpleui.modifiers.v3.M_Slider;
import simpleui.modifiers.v3.M_TextModifier;
import simpleui.modifiers.v3.M_Toolbar;
import simpleui.util.ColorUtils;
import simpleui.util.MenuItemList;
import simpleui.util.MenuItemList.MItem;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

public class M_CardViewTests extends M_Container {

	public M_CardViewTests() {
		add(new M_Toolbar("Material UI demo"));
		add(example1Card());
		add(newTestCard("A"));
		add(newTestCard("B"));
		add(newTestCard("C"));
		addTestModifiers();
		MenuItemList menuItemList = new MenuItemList();
		menuItemList.add(new MItem("Menu Item A", null) {
			@Override
			public void onClick(Activity context) {
				Toast.makeText(context, "Menu item A clicked",
						Toast.LENGTH_LONG).show();
			}
		});
		setMenuItemList(menuItemList);
	}

	private void addTestModifiers() {
		add(new M_TextModifier(true, false, false) {

			private String model = "Abc";

			@Override
			public boolean save(String newValue) {
				model = newValue;
				return true;
			}

			@Override
			public String load() {
				return model;
			}

			@Override
			public String getVarName() {
				return "M_TextInput example";
			}
		});
		add(new M_Checkbox() {

			@Override
			public boolean save(boolean arg0) {
				return true;
			}

			@Override
			public boolean loadVar() {
				return true;
			}

			@Override
			public CharSequence getVarName() {
				return "M_Checkbox example";
			}
		});
		add(new M_ProgressBar() {

			@Override
			public int loadMaxValue() {
				return 100;
			}

			@Override
			public int loadInitValue() {
				return 40;
			}

			@Override
			public String getVarName() {
				return "M_Progressbar example";
			}
		});

		add(new M_Slider(12) {
			@Override
			public int loadCurrentValue() {
				return 6;
			}

			@Override
			public boolean save(int arg0) {
				return true;
			}
		});
	}

	private ModifierInterface example1Card() {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("Welcome back!"));

		String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. ";
		c.add(new M_InfoText(text));
		c.add(M_SeperatorLine.newMaterialOne(null));
		M_Button left = new M_Button("No", true) {

			@Override
			public void onClick(Context arg0, Button arg1) {
				System.out.println("No");
			}
		};
		M_Button right = new M_Button("Yes", true) {

			@Override
			public void onClick(Context arg0, Button arg1) {
				System.out.println("Yes");
			}

		};
		c.add(new M_HalfHalf(left, right));

		return c;
	}

	private M_CardView newTestCard(final String cardName) {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("Card " + cardName));

		int color = ColorUtils.randomColor();
		c.add(M_SeperatorLine.newDefaultOne(color));
		c.setBackgroundColor(ColorUtils.getContrastVersionForColor(color));

		String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. ";
		c.add(new M_InfoText(text));
		c.add(new M_Button("Click me") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				Toast.makeText(context,
						"Button in card " + cardName + " was clicked",
						Toast.LENGTH_LONG).show();
			}
		});
		c.add(new M_InfoText(R.drawable.ic_dialog_alert, "Warning: " + text));
		return c;
	}

}
