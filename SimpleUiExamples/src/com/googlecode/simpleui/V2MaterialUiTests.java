package com.googlecode.simpleui;

import simpleui.modifiers.M_CardView;
import simpleui.modifiers.M_Container;
import simpleui.modifiers.M_Toolbar;
import v2.simpleUi.M_Button;
import v2.simpleUi.M_ButtonBorderless;
import v2.simpleUi.M_Caption;
import v2.simpleUi.M_Checkbox;
import v2.simpleUi.M_HalfHalf;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.M_SeperatorLine;
import v2.simpleUi.M_TextInput;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.util.ColorUtils;
import v3.MenuItemList;
import v3.MenuItemList.MItem;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class V2MaterialUiTests extends M_Container {

	public V2MaterialUiTests() {
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
		add(new M_TextInput(true, false, false) {

			@Override
			public boolean save(String arg0) {
				return true;
			}

			@Override
			public String load() {
				return "A";
			}

			@Override
			public String getVarName() {
				return "Aa";
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
				return "Bbbb";
			}
		});
	}

	private ModifierInterface example1Card() {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("Welcome back!"));

		String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. ";
		c.add(new M_InfoText(text));
		c.add(M_SeperatorLine.newMaterialOne(null));
		M_Button left = new M_Button("No") {

			@Override
			public void onClick(Context arg0, Button arg1) {
				System.out.println("No");
			}
		};
		M_ButtonBorderless right = new M_ButtonBorderless("Yes") {

			@Override
			public void onClick(Context arg0, View arg1) {
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
