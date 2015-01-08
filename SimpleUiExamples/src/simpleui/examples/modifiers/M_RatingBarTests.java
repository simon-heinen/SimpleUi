package simpleui.examples.modifiers;

import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_RatingBar;
import android.util.Log;

public class M_RatingBarTests extends M_Container {

	private final static String TAG = "M_RatingBarTests";

	public M_RatingBarTests() {
		M_InfoText infoText1 = new M_InfoText(
				"3 Stars, Stepsize 1, editable, default 1");
		M_RatingBar ratingBar1 = new M_RatingBar(true) {

			@Override
			public boolean save(float rating) {
				Log.i(TAG, "RatingBar1 saves : " + rating);
				return true;
			}

			@Override
			public String getVarName() {
				return "RatingBar1";
			}

			@Override
			public float getRating() {
				return 1;
			}

			@Override
			public int getMaxRatingValue() {
				return 3;
			}

			@Override
			public float getStepsize() {
				return 1;
			}
		};
		add(infoText1);
		add(ratingBar1);

		M_InfoText infoText2 = new M_InfoText(
				"3 Stars, Stepsize 0.5, editable, rating 2.5");
		M_RatingBar ratingBar2 = new M_RatingBar(true) {

			@Override
			public boolean save(float rating) {
				Log.i(TAG, "RatingBar1 saves : " + rating);
				return true;
			}

			@Override
			public String getVarName() {
				return "RatingBar2";
			}

			@Override
			public float getRating() {
				return 2.5f;
			}

			@Override
			public int getMaxRatingValue() {
				return 3;
			}

			@Override
			public float getStepsize() {
				return 0.5f;
			}
		};
		add(infoText2);
		add(ratingBar2);

	}

}
