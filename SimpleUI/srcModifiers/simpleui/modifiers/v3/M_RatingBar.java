package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public abstract class M_RatingBar implements ModifierInterface {

	private RatingBar ratingBar;
	private boolean editable = true;
	private float weightOfDescription = 1;
	private float weightOfRatingBar = 1;

	public M_RatingBar(boolean editable) {
		super();
		this.editable = editable;
	}

	@Override
	public View getView(Context context) {
		LinearLayout container = new LinearLayout(context);
		container.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		container.setGravity(Gravity.CENTER_VERTICAL);
		container.setOrientation(LinearLayout.VERTICAL);
		LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfDescription);
		LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				weightOfRatingBar);

		TextView nameText = new TextView(context);
		nameText.setText(getVarName());
		nameText.setLayoutParams(p);
		container.addView(nameText);

		ratingBar = new RatingBar(context, null, android.R.attr.ratingBarStyle);
		ratingBar.setLayoutParams(p2);
		ratingBar.setStepSize(getStepsize());
		ratingBar.setNumStars(getMaxRatingValue());
		ratingBar.setIsIndicator(!editable);
		ratingBar.setRating(getRating());

		container.addView(ratingBar);

		return container;
	}

	public void setWeightOfDescription(float weightOfDescription) {
		this.weightOfDescription = weightOfDescription;
	}

	public void setWeightOfRatingBar(float weightOfRatingBar) {
		this.weightOfRatingBar = weightOfRatingBar;
	}

	@Override
	public boolean save() {
		if (ratingBar.getRating() < getMinRating()) {
			return save(1);
		}
		return save(ratingBar.getRating());
	}

	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param rating
	 *            in percent
	 */
	public void setRating(float rating) {
		if (ratingBar != null) {
			this.ratingBar.setRating(rating);
		}
	}

	public abstract float getRating();

	public abstract int getMaxRatingValue();

	public abstract boolean save(float rating);

	public abstract String getVarName();

	public abstract float getStepsize();

	public float getMinRating() {
		return 1;
	}

}
