package simpleui.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;

public class ColorUtils extends Color {

	private static final String LOG_TAG = ColorUtils.class.getSimpleName();
	/**
	 * Try Orientation.TL_BR or Orientation.TOP_BOTTOM and so on
	 */
	private Orientation gradientOrientation;
	/**
	 * is final because the colors in the theme can be changed but not the
	 * ColorTheme itself
	 */
	private final float[] cornerRadii;
	private final int[] colorsInGradient;
	private Float gradientRadius;

	public final static int l2pBlueDark = Color.argb(255, 0, 110, 200);
	public final static int l2pBlue = Color.argb(255, 63, 149, 226);
	public final static int l2pBlueLight = Color.argb(255, 105, 170, 226);
	public final static int l2pGray = Color.argb(255, 136, 136, 173);
	public final static int l2pGrayLight = Color.argb(255, 236, 236, 236);
	public final static int l2pOrange = Color.argb(255, 202, 112, 33);

	public final static int C1_GrayDark = Color.argb(255, 74, 71, 71);
	public final static int C1_blueLight = Color.argb(255, 125, 182, 213);
	public final static int C1_redDark = Color.argb(255, 157, 46, 44);
	public final static int C1_yellowLight = Color.argb(255, 249, 234, 153);
	public final static int C1_yellowDark = Color.argb(255, 231, 165, 85);

	public final static int C2_orange = Color.argb(255, 254, 132, 2);
	public final static int C2_green = Color.argb(255, 65, 146, 75);
	public final static int C2_blue = Color.argb(255, 81, 165, 186);

	public final static int C3_red = Color.argb(255, 163, 30, 57);
	public final static int C3_darkGray = Color.argb(255, 72, 92, 90);
	public final static int C3_gray = Color.argb(255, 140, 156, 154);

	public final static int C4_greenDark = Color.argb(255, 92, 117, 94);
	public final static int C4_brown = Color.argb(255, 169, 125, 93);
	public final static int C4_brownLight = Color.argb(255, 247, 220, 180);

	public final static int C5_red = Color.argb(255, 255, 51, 51);
	public final static int C5_blue = Color.argb(255, 102, 153, 153);
	public final static int C5_blueDark = Color.argb(255, 0, 51, 51);

	public final static int C6_orange = Color.argb(255, 241, 108, 50);
	public final static int C6_grayLight = Color.argb(255, 247, 246, 241);
	public final static int C6_gray = Color.argb(255, 200, 200, 195);

	public final static int C7_grayDark = Color.argb(255, 68, 79, 88);
	public final static int C7_gray = Color.argb(255, 119, 132, 140);
	public final static int C7_grayLight = Color.argb(255, 200, 206, 211);

	/**
	 * @param alpha
	 *            [0..255]
	 * @param red
	 *            [0..255]
	 * @param green
	 *            [0..255]
	 * @param blue
	 *            [0..255]
	 * @return the calculated argb value
	 */
	public static int toARGB(int alpha, int red, int green, int blue) {
		return android.graphics.Color.argb(alpha, red, green, blue);
	}

	/**
	 * @param colorString
	 *            #RRGGBB or #AARRGGBB or... read
	 *            {@link Color#parseColor(String)}
	 * @return
	 */
	public static int toARGB(String colorString) {
		return android.graphics.Color.parseColor(colorString);
	}

	public static int[] createBlackGradient1(int alpha) {
		int[] c = new int[3];
		int f = 0;
		c[0] = toARGB(alpha, 120 + f, 120 + f, 120 + f);
		c[1] = toARGB(alpha, 70 + f, 70 + f, 70 + f);
		c[2] = toARGB(alpha, 20 + f, 20 + f, 20 + f);
		return c;
	}

	public static int[] createBlackGradient2() {
		int[] c = new int[2];
		int f = 0;
		int alpha = 255;
		c[1] = toARGB(alpha, 28 + f, 33 + f, 37 + f);
		c[0] = toARGB(alpha, 54 + f, 69 + f, 76 + f);
		return c;
	}

	public static int[] createGrayGradient1() {
		int[] c = new int[3];
		int f = -20;
		c[0] = toARGB(255, 90 + f, 90 + f, 90 + f);
		c[1] = toARGB(255, 70 + f, 70 + f, 70 + f);
		c[2] = toARGB(255, 50 + f, 50 + f, 50 + f);
		return c;
	}

	public static int[] createGrayGradient2() {
		int[] c = new int[3];
		int f = -40;
		c[0] = toARGB(255, 190 + f, 190 + f, 190 + f);
		c[1] = toARGB(255, 185 + f, 185 + f, 185 + f);
		c[2] = toARGB(255, 160 + f, 160 + f, 160 + f);
		return c;
	}

	public static int[] createGrayGradient3() {
		int[] c = new int[3];
		int f = -40;
		c[0] = toARGB(0, 160 + f, 160 + f, 160 + f);
		c[1] = toARGB(255, 185 + f, 185 + f, 185 + f);
		c[2] = toARGB(0, 160 + f, 160 + f, 160 + f);
		return c;
	}

	public static int[] createRainbowGradient() {
		int[] colorArray = new int[3];
		int alpha = 255;
		colorArray[0] = toARGB(alpha, 255, 10, 10);
		colorArray[1] = toARGB(alpha, 10, 255, 10);
		colorArray[2] = toARGB(alpha, 10, 10, 255);
		return colorArray;
	}

	public static int[] createGreenGradient() {
		int[] colorArray = new int[5];
		int alpha = 160;
		colorArray[0] = toARGB(alpha, 0, 95, 0);
		colorArray[1] = toARGB(alpha, 0, 95, 0);
		colorArray[2] = toARGB(alpha, 0, 110, 0);
		colorArray[3] = toARGB(alpha, 0, 115, 0);
		colorArray[4] = toARGB(alpha, 0, 120, 0);
		return colorArray;
	}

	public static int[] createRedGradient() {
		int[] colorArray = new int[5];
		int alpha = 160;
		int x = 120;
		colorArray[0] = toARGB(alpha, x + 95, 0, 0);
		colorArray[1] = toARGB(alpha, x + 95, 0, 0);
		colorArray[2] = toARGB(alpha, x + 110, 0, 0);
		colorArray[3] = toARGB(alpha, x + 115, 0, 0);
		colorArray[4] = toARGB(alpha, x + 120, 0, 0);
		return colorArray;
	}

	public void applyBackgroundTo(View v) {
		if (colorsInGradient != null && cornerRadii != null) {

			if (gradientOrientation == null) {
				gradientOrientation = Orientation.TOP_BOTTOM;
			}
			GradientDrawable s = new GradientDrawable(gradientOrientation,
					colorsInGradient);
			if (gradientRadius != null) {
				s.setGradientType(GradientDrawable.RADIAL_GRADIENT);
				s.setGradientCenter(0.5f, 0.4f);
				s.setGradientRadius(gradientRadius);
			}
			s.setCornerRadii(cornerRadii);
			v.setBackgroundDrawable(s);
		}
	}

	public static int randomColor() {
		return Color.rgb((int) (Math.random() * 255f),
				(int) (Math.random() * 255f), (int) (Math.random() * 255f));
	}

	/**
	 * @param leftTop
	 * @param rightTop
	 * @param rightBottom
	 * @param leftBottom
	 * @return
	 */
	public static float[] genCornerArray(int leftTop, int rightTop,
			int rightBottom, int leftBottom) {
		float[] a = new float[8];
		int i = 0;
		a[i++] = leftTop;
		a[i++] = leftTop;
		a[i++] = rightTop;
		a[i++] = rightTop;
		a[i++] = rightBottom;
		a[i++] = rightBottom;
		a[i++] = leftBottom;
		a[i++] = leftBottom;
		return a;
	}

	/**
	 * 
	 * uses the HSP color model ( http://alienryderflex.com/hsp.html ).
	 * 
	 * @param color
	 * @return range from 0 (for black) to 255 (for white)
	 */
	public static float getColorBrightness(int color) {
		float R = Color.red(color);
		float G = Color.green(color);
		float B = Color.blue(color);
		return 0.299f * R + 0.587f * G + 0.114f * B;
	}

	public static int getComplementaryColor(int colorToInvert) {
		float[] hsv = new float[3];
		Color.RGBToHSV(Color.red(colorToInvert), Color.green(colorToInvert),
				Color.blue(colorToInvert), hsv);
		hsv[0] = (hsv[0] + 180) % 360;
		return Color.HSVToColor(hsv);
	}

	/**
	 * will calculate a color with the same color hue just a darker/lighter
	 * version of it
	 * 
	 * @param color
	 * @return
	 */
	public static int getContrastVersionForColor(int color) {
		float[] hsv = new float[3];
		Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color),
				hsv);
		if (hsv[2] < 0.5) {
			hsv[2] = 0.7f;
		} else {
			hsv[2] = 0.3f;
		}
		hsv[1] = hsv[1] * 0.2f;
		return Color.HSVToColor(hsv);
	}

	public static int getDefaultBackgroundColor(Context context,
			int fallbackColor) {
		try {
			TypedArray array = context.getTheme().obtainStyledAttributes(
					new int[] { android.R.attr.colorBackground });
			int defaultBackgroundColor = array.getColor(0, fallbackColor);
			return defaultBackgroundColor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fallbackColor;
	}

	public static float[] genCornerArray(int cornerSize) {
		return genCornerArray(cornerSize, cornerSize, cornerSize, cornerSize);
	}

	public static ColorUtils newBackRadialBackgroundWithTransparency(int size,
			int alpha) {
		return new ColorUtils(size, ColorUtils.createBlackGradient2(),
				ColorUtils.genCornerArray(17));
	}

	public static ColorUtils newGrayBackgroundTransparent() {
		return new ColorUtils(Orientation.BL_TR,
				ColorUtils.createBlackGradient1(150),
				ColorUtils.genCornerArray(15));
	}

	public static ColorUtils newGrayBackground() {
		return new ColorUtils(Orientation.BL_TR,
				ColorUtils.createGrayGradient1(), ColorUtils.genCornerArray(15));
	}

	public static ColorUtils newRedBackground() {
		return new ColorUtils(Orientation.BL_TR,
				ColorUtils.createRedGradient(), ColorUtils.genCornerArray(10));
	}

	public static ColorUtils newGreenBackground() {
		return new ColorUtils(Orientation.BL_TR,
				ColorUtils.createGreenGradient(), ColorUtils.genCornerArray(10));
	}

	/**
	 * @param o
	 *            use the {@link Orientation} class
	 * @param colorsInGradient
	 *            see {@link ColorUtils#createGrayGradient1()} for
	 *            implementation details
	 * @param cornerRadii
	 *            use {@link ColorUtils#genCornerArray(int)}
	 */
	public ColorUtils(float gradientRadius, int[] colorsInGradient,
			float[] cornerRadii) {
		this.colorsInGradient = colorsInGradient;
		this.cornerRadii = cornerRadii;
		this.gradientRadius = gradientRadius;
	}

	/**
	 * @param o
	 *            use the {@link Orientation} class
	 * @param colorsInGradient
	 *            see {@link ColorUtils#createGrayGradient1()} for
	 *            implementation details
	 * @param cornerRadii
	 *            use {@link ColorUtils#genCornerArray(int)}
	 */
	public ColorUtils(Orientation o, int[] colorsInGradient, float[] cornerRadii) {
		this.gradientOrientation = o;
		this.colorsInGradient = colorsInGradient;
		this.cornerRadii = cornerRadii;
	}

}
