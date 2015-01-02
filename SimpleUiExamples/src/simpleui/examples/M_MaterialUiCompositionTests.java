package simpleui.examples;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_CardView;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_HalfHalf;
import simpleui.modifiers.v3.M_ImageView;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_SeperatorLine;
import simpleui.modifiers.v3.M_Toolbar;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Button;
import android.widget.Toast;

public class M_MaterialUiCompositionTests extends M_Container {

	private static void toast(Context c, String text) {
		Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
	}

	public M_MaterialUiCompositionTests() {
		setCardBackgroundColor(Color.DKGRAY);
		add(new M_Toolbar("Material UI examples"));
		add(newM_CardViewAndM_InfoTextExamples());
	}

	private static M_CardView newM_CardViewAndM_InfoTextExamples() {
		M_CardView c = new M_CardView();
		M_ImageView imageView = new M_ImageView(
				Uri.parse("http://lorempixel.com/500/200/"));
		imageView.setImageCaption("Kangaroo Valley Safari");
		c.add(imageView);
		c.add(new M_InfoText(
				"Located two hours south of Sydney in the Southern Highlands of New South Wales, ..."));
		c.add(M_SeperatorLine.newMaterialOne(null));

		ModifierInterface left = new M_Button("SHARE", true) {

			@Override
			public void onClick(Context context, Button arg1) {
				toast(context, "SHARE clicked");
			}
		};
		ModifierInterface right = new M_Button("EXPLORE", true) {

			@Override
			public void onClick(Context context, Button arg1) {
				toast(context, "EXPLORE clicked");
			}
		};
		M_HalfHalf buttons = new M_HalfHalf(left, right);
		c.add(M_HalfHalf.GoldenCutLeftLarge(buttons, null));

		return c;
	}
}
