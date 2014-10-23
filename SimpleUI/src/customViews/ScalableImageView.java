package customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScalableImageView extends ImageView {

	public ScalableImageView(Context context) {
		super(context);
	}

	public ScalableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScalableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);

		int height = 0;
		if (getDrawable() != null) {
			height = width * getDrawable().getIntrinsicHeight()
					/ getDrawable().getIntrinsicWidth();
		} else {
			height = getMinimumHeight();
		}

		setMeasuredDimension(width, height);
	}
}
