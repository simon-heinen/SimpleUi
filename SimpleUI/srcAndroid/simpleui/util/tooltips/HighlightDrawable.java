package simpleui.util.tooltips;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * draws a cicle arount a target view (set that view via
 * {@link HighlightDrawable#setHighlightArea(View)})
 * 
 */
class HighlightDrawable extends Drawable {

	private float sizeFactor = 1.5f;
	private final Paint mPaint;
	private float circleX;
	private float circleY;
	private float radius;
	private final View overlayContainer;
	private View viewToHighlight;
	private Integer circleRadiusInPixel;

	/**
	 * @param overlayContainer
	 *            the container which gets this {@link Drawable} as the
	 *            background
	 * @param overlayColor
	 *            see {@link Color}
	 * 
	 */
	public HighlightDrawable(View overlayContainer, int overlayColor) {
		mPaint = new Paint();
		setOverlayColor(overlayColor);
		this.overlayContainer = overlayContainer;
	}

	/**
	 * @param circleRadiusInPixel
	 *            if null the radius will be calculated automatically based on
	 *            the width and height of the target view
	 */
	public void setCircleRadiusInPixel(Integer circleRadiusInPixel) {
		this.circleRadiusInPixel = circleRadiusInPixel;
		invalidateSelf();
	}

	public void setOverlayColor(int overlayColor) {
		mPaint.setColor(overlayColor);
		invalidateSelf();
	}

	/**
	 * @param sizeFactor
	 *            default is 1.5f so that the circle is a little bit bigger than
	 *            the target view itself so that the target view is fully inside
	 *            the circle
	 */
	public void setSizeFactor(float sizeFactor) {
		this.sizeFactor = sizeFactor;
		invalidateSelf();
	}

	@Override
	public void draw(Canvas canvas) {
		if (viewToHighlight != null) {
			int[] viewPos = new int[2];
			int[] parentPos = new int[2];
			viewToHighlight.getLocationOnScreen(viewPos);
			overlayContainer.getLocationOnScreen(parentPos);
			circleX = viewPos[0] - parentPos[0];
			circleY = viewPos[1] - parentPos[1];
			circleX += viewToHighlight.getWidth() / 2;
			circleY += viewToHighlight.getHeight() / 2;
			radius = (viewToHighlight.getWidth() + viewToHighlight.getHeight())
					/ 4f * sizeFactor;
			if (circleRadiusInPixel != null) {
				radius = circleRadiusInPixel;
			}
		}
		Path path = new Path();
		// first draw fills, second removes, and so on..
		path.setFillType(FillType.EVEN_ODD);
		Rect rec = getBounds();
		float l = rec.left;
		float t = rec.top;
		float r = rec.right;
		float b = rec.bottom;
		// fills
		path.addRect(l, t, r, b, Path.Direction.CW);
		// removes
		path.addCircle(circleX, circleY, radius, Path.Direction.CCW);
		canvas.drawPath(path, mPaint);
	}

	public void setHighlightArea(View viewToHighlight) {
		this.viewToHighlight = viewToHighlight;
		invalidateSelf();
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

}
