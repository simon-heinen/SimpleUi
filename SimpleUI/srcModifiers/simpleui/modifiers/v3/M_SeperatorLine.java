package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.util.ColorUtils;
import simpleui.util.ImageTransform;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public abstract class M_SeperatorLine implements ModifierInterface {

	private LinearLayout line;
	protected Integer backgroundColor;
	protected int margins = 10;

	@Override
	public View getView(Context context) {
		line = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				(int) ImageTransform.dipToPixels(line.getResources(),
						getHeigthInDip()));
		lp.setMargins(margins, 2 * margins, margins, 2 * margins);
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
		M_SeperatorLine l = new M_SeperatorLine() {

			@Override
			public Integer getHeigthInDip() {
				return 2;
			}
		};
		l.backgroundColor = color;
		return l;
	}

	public static M_SeperatorLine newMaterialOne() {
		return newMaterialOne(null);
	}

	/**
	 * @param color
	 *            can be null, then a contrast version of the default background
	 *            color will be picked automatically
	 * @return
	 */
	public static M_SeperatorLine newMaterialOne(final Integer color) {
		M_SeperatorLine l = new M_SeperatorLine() {
			@Override
			public View getView(Context context) {
				if (color != null) {
					backgroundColor = color;
				} else {
					int bgColor = ColorUtils.getDefaultBackgroundColor(context,
							Color.BLACK);
					backgroundColor = ColorUtils
							.getContrastVersionForColor(bgColor);
				}
				return super.getView(context);
			}

			@Override
			public Integer getHeigthInDip() {
				return 1;
			}
		};
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
