package simpleui.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
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

	@SuppressLint("NewApi")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);

		int height = 0;
		if (getDrawable() != null) {
			int intrinsicWidth = getDrawable().getIntrinsicWidth();
			if (intrinsicWidth != 0) {
				height = width * getDrawable().getIntrinsicHeight()
						/ intrinsicWidth;
			}
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				height = getMinimumHeight();
			} else {
				height = 1;
			}

		}
		setMeasuredDimension(width, height);
	}
}
