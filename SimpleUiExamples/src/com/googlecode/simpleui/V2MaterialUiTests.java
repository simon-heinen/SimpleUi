package com.googlecode.simpleui;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_Caption;
import v2.simpleUi.M_CardView;
import v2.simpleUi.M_Container;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.M_Toolbar;
import v3.MenuItemList;
import v3.MenuItemList.MItem;
import android.R;
import android.content.Context;
import android.widget.Button;

public class V2MaterialUiTests extends M_Container {

	public V2MaterialUiTests() {

		MenuItemList menuItemList = new MenuItemList();
		menuItemList.add(new MItem("Options", null) {

			@Override
			public void onClick() {
				System.out.println("Options clicked");
			}

		});
		setMenuItemList(menuItemList);

		add(new M_Toolbar("Material UI demo"));

		add(newTestCard());
		add(newTestCard());
		add(newTestCard());

	}

	private M_CardView newTestCard() {
		M_CardView c = new M_CardView();
		c.add(new M_InfoText("Aaaaa"));
		c.add(new M_InfoText("Bbbbb"));
		c.add(new M_InfoText(R.drawable.ic_dialog_alert, "Ccccc"));
		c.add(new M_Caption("Aaaa"));
		c.add(new M_Button("Click") {

			@Override
			public void onClick(Context context, Button clickedButton) {
			}
		});
		return c;
	}

}
