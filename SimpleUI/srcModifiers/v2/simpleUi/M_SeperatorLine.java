package v2.simpleUi;

import tools.ImageTransform;
import v2.simpleUi.util.ColorUtils;
import android.content.Context;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public abstract class M_SeperatorLine implements ModifierInterface {

	private LinearLayout line;
	private Integer backgroundColor;

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
		if (backgroundColor != null) {
			line.setBackgroundColor(backgroundColor);
		} else {
			loadBGUtils().applyBackgroundTo(line);
		}
		return line;
	}

	public ColorUtils loadBGUtils() {
		int[] colorsInGradient = ColorUtils.createGrayGradient3();
		ColorUtils bgUtils = new ColorUtils(Orientation.LEFT_RIGHT,
				colorsInGradient, ColorUtils.genCornerArray(2));
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

	public static M_SeperatorLine newDefaultOne(int color) {
		M_SeperatorLine l = newDefaultOne();
		l.backgroundColor = color;
		return l;
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
