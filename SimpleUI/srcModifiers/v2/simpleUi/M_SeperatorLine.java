package v2.simpleUi;

import tools.ImageTransform;
import v2.simpleUi.util.BGUtils;
import android.content.Context;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public abstract class M_SeperatorLine implements ModifierInterface {

	private LinearLayout line;

	@Override
	public View getView(Context context) {
		line = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				(int) ImageTransform.dipToPixels(line.getResources(),
						getHeigthInDip()));
		int p = 10;
		lp.setMargins(p, 2 * p, p, 2 * p);
		line.setLayoutParams(lp);
		loadBGUtils().applyTo(line);
		return line;
	}

	public BGUtils loadBGUtils() {
		int[] colorsInGradient = BGUtils.createGrayGradient3();
		BGUtils bgUtils = new BGUtils(Orientation.LEFT_RIGHT, colorsInGradient,
				BGUtils.genCornerArray(2));
		return bgUtils;
	}

	public LinearLayout getLine() {
		return line;
	}

	/**
	 * @return 5 e.g.
	 */
	public abstract Integer getHeigthInDip();

	@Override
	public boolean save() {
		return true;
	}

	public static M_SeperatorLine newDefaultOne() {
		return new M_SeperatorLine() {

			@Override
			public Integer getHeigthInDip() {
				return 3;
			}
		};
	}

}
