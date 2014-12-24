package simpleui.util.tooltips;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * draws a chat bubble, the position of the arrow of the chat bubble can be
 * specified
 * 
 */
public class ChatBubbleDrawable extends Drawable {

	/**
	 * 0=center 1=left 2=right
	 */
	private final int arrowAlignment;
	private final boolean arrowOnTop;
	private Paint paint;
	private final int trSize;
	private final int cr;

	/**
	 * @param arrowAlignment
	 *            0=center 1=left 2=right
	 * @param arrowOnTop
	 *            if true the arrow should be on top of the bubble
	 * @param color
	 *            see {@link Color}
	 * @param triangleSizeInPixel
	 *            e.g. 40
	 * @param cornerRadiusInPixel
	 *            e.g. 20
	 */
	public ChatBubbleDrawable(int arrowAlignment, boolean arrowOnTop,
			int color, int triangleSizeInPixel, int cornerRadiusInPixel) {
		this.arrowAlignment = arrowAlignment;
		this.arrowOnTop = arrowOnTop;
		this.trSize = triangleSizeInPixel;
		this.cr = cornerRadiusInPixel;
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	@Override
	public void draw(Canvas canvas) {
		int hs = trSize / 2; // half of trinagle hypotenuse
		int pad = hs; // same as hs so that trinagle not cut off
		RectF rect = new RectF(0 + pad, 0 + pad, canvas.getWidth() - pad,
				canvas.getHeight() - pad);

		canvas.drawRoundRect(rect, cr, cr, paint);

		Path triangle = new Path();

		int x = canvas.getWidth() / 2 - trSize / 2; // center
		if (arrowAlignment == 1) { // left
			x = 0 + pad + cr;
		}
		if (arrowAlignment == 2) { // right
			x = canvas.getWidth() - pad - trSize - cr;
		}
		int y;
		if (arrowOnTop) {
			y = 0 + pad;
		} else {
			y = canvas.getHeight() - pad;
		}
		Point a = new Point(x, y);
		Point b = new Point(x + trSize, y);
		Point c = new Point(x + hs, arrowOnTop ? y - hs : y + hs);
		triangle.moveTo(a.x, a.y);
		triangle.lineTo(b.x, b.y);
		triangle.lineTo(c.x, c.y);
		triangle.lineTo(a.x, a.y);
		canvas.drawPath(triangle, paint);

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
