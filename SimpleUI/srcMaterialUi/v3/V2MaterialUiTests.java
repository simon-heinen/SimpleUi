package v3;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_Caption;
import v2.simpleUi.M_InfoText;
import v3.MenuItemList;
import v3.MenuItemList.MItem;
import android.R;
import android.content.Context;
import android.widget.Button;

public class V2MaterialUiTests extends M_Container4 {

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
		add(new M_InfoText("Aaaaa"));
		add(new M_InfoText("Bbbbb"));
		add(new M_InfoText(R.drawable.ic_dialog_alert, "Ccccc"));
		add(new M_Caption("Aaaa"));
		add(new M_Button("Click") {

			@Override
			public void onClick(Context context, Button clickedButton) {
			}
		});
		add(new M_InfoText("Aaaaa"));
		add(new M_InfoText("Bbbbb"));
		add(new M_InfoText(R.drawable.ic_dialog_alert, "Ccccc"));
		add(new M_Caption("Aaaa"));
		add(new M_Button("Click") {

			@Override
			public void onClick(Context context, Button clickedButton) {
			}
		});
		add(new M_InfoText("Aaaaa"));
		add(new M_InfoText("Bbbbb"));
		add(new M_InfoText(R.drawable.ic_dialog_alert, "Ccccc"));
		add(new M_Caption("Aaaa"));
		add(new M_Button("Click") {

			@Override
			public void onClick(Context context, Button clickedButton) {
			}
		});

	}

}
