package simpleui.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Not yet working
 */
@Deprecated
public class ParallaxImageView extends ImageView {

	float offset = -0.5f;

	public ParallaxImageView(Context context) {
		super(context);
	}

	public ParallaxImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParallaxImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (getDrawable() != null) {
			canvas.translate(0,
					(offset * getDrawable().getIntrinsicHeight()) * 0.9f);
		}

		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (getParent().getParent().getParent() instanceof ListView) {
			final ListView lv = (ListView) getParent().getParent().getParent();
			final View parent = (View) getParent().getParent();
			lv.getViewTreeObserver().addOnScrollChangedListener(
					new ViewTreeObserver.OnScrollChangedListener() {
						@Override
						public void onScrollChanged() {
							offset = ((float) parent.getTop() / (float) lv
									.getMeasuredHeight()) / 2f;

							if (offset <= -0.5f) {
								offset = -0.5f;
							} else if (offset >= 0.5f) {
								offset = 0.5f;
							}

							ParallaxImageView.this.invalidate();
						}
					});
		}
	}
}
