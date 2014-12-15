package com.googlecode.simpleui;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_Caption;
import v2.simpleUi.M_CardView;
import v2.simpleUi.M_Container;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.M_SeperatorLine;
import v2.simpleUi.M_Toolbar;
import v2.simpleUi.util.ColorUtils;
import v3.MenuItemList;
import v3.MenuItemList.MItem;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

public class V2MaterialUiTests extends M_Container {

	public V2MaterialUiTests() {
		add(new M_Toolbar("Material UI demo"));
		add(newTestCard("A"));
		add(newTestCard("B"));
		add(newTestCard("C"));
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

	private M_CardView newTestCard(final String cardName) {
		M_CardView c = new M_CardView();
		c.add(new M_Caption("Card " + cardName));

		int color = ColorUtils.randomColor();
		c.add(M_SeperatorLine.newDefaultOne(color));
		c.setBackgroundColor(ColorUtils.getComplementaryColor(color));

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
