package simpleui.customViews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Not yet working. use android:scaleType="centerCrop" on your normal
 * {@link ImageView} for now
 * 
 * Will display the contained drawable always without borders (in fullscreen
 * mode). This view calculates its size manually so xml attributes will be
 * ignored!
 */
@Deprecated
public class FillParentWithAspectRatioImageView extends ImageView {

	public FillParentWithAspectRatioImageView(Context context) {
		super(context);
	}

	public FillParentWithAspectRatioImageView(Context context,
			AttributeSet attrs) {
		super(context, attrs);
	}

	public FillParentWithAspectRatioImageView(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable d = getDrawable();
		if (d == null) {
			d = getBackground();
		}
		if (d != null) {
			int ih = d.getIntrinsicHeight();
			int iw = d.getIntrinsicWidth();
			if (ih / iw > heightMeasureSpec / widthMeasureSpec) {
				int width = MeasureSpec.getSize(widthMeasureSpec);
				int height = width * ih / iw;
				setMeasuredDimension(width, height);
			} else {
				int height = MeasureSpec.getSize(heightMeasureSpec);
				int width = height * iw / ih;
				setMeasuredDimension(width, height);
			}
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
