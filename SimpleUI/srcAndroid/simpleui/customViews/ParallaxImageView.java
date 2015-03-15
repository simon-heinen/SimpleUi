package simpleui.customViews;

import simpleui.util.Pair;
import util.Log;
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

	private static final String LOG_TAG = ParallaxImageView.class
			.getSimpleName();
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
		Pair<View, View> p = searchForAncestorOfType(ListView.class, this);
		// if (p == null) {
		// Log.d(LOG_TAG, "getListView(FreeFlowContainer," + this + ")");
		// p = getParentOfType(FreeFlowContainer.class, this);
		// }
		if (p != null) {
			final View listView = p.getA();
			final View mostOuterParentOfImage = p.getB();
			listView.getViewTreeObserver().addOnScrollChangedListener(
					new ViewTreeObserver.OnScrollChangedListener() {
						@Override
						public void onScrollChanged() {
							offset = ((float) mostOuterParentOfImage.getTop() / (float) listView
									.getMeasuredHeight()) / 2f;
							if (offset <= -0.5f) {
								offset = -0.5f;
							} else if (offset >= 0.5f) {
								offset = 0.5f;
							}
							ParallaxImageView.this.invalidate();
						}
					});
		} else {
			Log.w(LOG_TAG, LOG_TAG + " was not inside a list view, "
					+ "paralax effect will not work");
		}
	}

	private <T> Pair<T, View> searchForAncestorOfType(Class acestorType, View v) {
		if (acestorType.isAssignableFrom(v.getParent().getClass())) {
			return new Pair<T, View>((T) v.getParent(), v);
		} else if (v.getParent() instanceof View) {
			return searchForAncestorOfType(acestorType, (View) v.getParent());
		}
		return null;
	}
}
