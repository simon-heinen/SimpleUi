package simpleui.examples;

import java.util.ArrayList;
import java.util.List;

import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_CardView;
import simpleui.modifiers.v3.M_Checkbox;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_RadioButtonList;
import simpleui.modifiers.v3.M_RadioButtonList.DefaultSelectableItem;
import simpleui.modifiers.v3.M_SeperatorLine;
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

			@Override
			public boolean save(DefaultSelectableItem i) {
				return true;
			}

			@Override
			public void onItemSelectedByUser(Context c, DefaultSelectableItem i) {
			}

			@Override
			public List<DefaultSelectableItem> getItemList() {
				List<DefaultSelectableItem> l = new ArrayList<DefaultSelectableItem>();
				l.add(new DefaultSelectableItem(0, "M_RadioButtonList Item 1"));
				l.add(new DefaultSelectableItem(1, "M_RadioButtonList Item 2"));
				return l;
			}
		});
		return c;
	}

	private static void toast(Context c, String text) {
		Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
	}

}
