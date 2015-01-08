package simpleui.customViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * modified version from
 * https://github.com/Espiandev/ShowcaseView/blob/master/library
 * /src/com/espian/showcaseview/ShowcaseView.java
 * 
 * A view which allows you to showcase areas of your app with an explanation.
 */
public abstract class ShowcaseView extends RelativeLayout implements
		View.OnClickListener, View.OnTouchListener {

	public static final int TYPE_NO_LIMIT = 0;
	public static final int TYPE_ONE_SHOT = 1;

	private final String INTERNAL_PREFS = "showcase_internal";
	private final String SHOT_PREF_STORE = "hasShot";

	float showcaseX = -1, showcaseY = -1;
	int shotType = TYPE_NO_LIMIT;
	boolean isRedundant = false;
	boolean block = true;
	float showcaseRadius = -1;

	Paint background;
	int backColor;
	Drawable showcase;
	View mButton;
	OnClickListener mListener;
	OnShowcaseEventListener mEventListener;

	public ShowcaseView(Context context) {
		this(context, null, 0);
	}

	public ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init() {
		boolean hasShot = getContext().getSharedPreferences(INTERNAL_PREFS,
				Context.MODE_PRIVATE).getBoolean(SHOT_PREF_STORE, false);
		if (hasShot && shotType == TYPE_ONE_SHOT) {
			// The showcase has already been shot once, so we don't need to do
			// anything
			setVisibility(View.GONE);
			isRedundant = true;
			return;
		}
		background = new Paint();
		background.setColor(backColor);
		showcase = getShowCaseImage();
		mButton = getButton();
		if (mButton != null) {
			mButton.setOnClickListener(this);
		}
		float dens = getResources().getDisplayMetrics().density;
		showcaseRadius = dens * 94;
		setOnTouchListener(this);
	}

	public abstract Drawable getShowCaseImage();

	public abstract View getButton();

	/**
	 * Set the view to showcase
	 * 
	 * @param view
	 *            The {@link View} to showcase.
	 */
	public void setShowcaseView(final View view) {
		if (isRedundant || view == null) {
			isRedundant = true;
			return;
		}
		isRedundant = false;

		view.post(new Runnable() {
			@Override
			public void run() {
				init();
				showcaseX = view.getLeft() + view.getWidth() / 2;
				showcaseY = view.getTop() + view.getHeight() / 2;
				invalidate();
			}
		});
	}

	/**
	 * Set a specific position to showcase
	 * 
	 * @param x
	 * @param y
	 */
	public void setShowcasePosition(float x, float y) {
		if (isRedundant) {
			return;
		}
		showcaseX = x;
		showcaseY = y;
		invalidate();
	}

	/**
	 * Set the shot method of the showcase - only once or no limit
	 * 
	 * @param shotType
	 *            either TYPE_ONE_SHOT or TYPE_NO_LIMIT
	 */
	public void setShotType(int shotType) {
		if (shotType == TYPE_NO_LIMIT || shotType == TYPE_ONE_SHOT) {
			this.shotType = shotType;
		}
	}

	/**
	 * Decide whether touches outside the showcased circle should be ignored or
	 * not
	 * 
	 * @param block
	 *            true to block touches, false otherwise. By default, this is
	 *            true.
	 */
	public void blockNonShowcasedTouches(boolean block) {
		this.block = block;
	}

	/**
	 * Override the standard button click event, if there is a button available
	 * 
	 * @param listener
	 *            Listener to listen to on click events
	 */
	public void overrideButtonClick(OnClickListener listener) {
		if (isRedundant) {
			return;
		}
		if (mButton != null) {
			mButton.setOnClickListener(listener);
		}
	}

	public void setOnShowcaseEventListener(OnShowcaseEventListener listener) {
		mEventListener = listener;
	}

	public void removeOnShowcaseEventListener() {
		setOnClickListener(null);
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		if (showcaseX < 0 || showcaseY < 0 || isRedundant) {
			super.dispatchDraw(canvas);
			return;
		}

		Bitmap b = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);

		// Draw the semi-transparent background
		c.drawColor(backColor);

		// Erase the area for the ring
		Paint eraser = new Paint();
		eraser.setColor(0xFFFFFF);
		eraser.setAlpha(0);
		eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
		c.drawCircle(showcaseX, showcaseY, showcaseRadius, eraser);

		int cx = (int) showcaseX, cy = (int) showcaseY;
		int dw = showcase.getIntrinsicWidth();
		int dh = showcase.getIntrinsicHeight();

		showcase.setBounds(cx - dw / 2, cy - dh / 2, cx + dw / 2, cy + dh / 2);
		showcase.draw(c);

		canvas.drawBitmap(b, 0, 0, null);
		c.setBitmap(null);
		b = null;

		super.dispatchDraw(canvas);

	}

	@Override
	public void onClick(View view) {
		// If the type is set to one-shot, store that it has shot
		if (shotType == TYPE_ONE_SHOT) {
			SharedPreferences internal = getContext().getSharedPreferences(
					"showcase_internal", Context.MODE_PRIVATE);
			internal.edit().putBoolean("hasShot", true).commit();
		}

		if (mListener == null) {
			hide();
		} else {
			mListener.onClick(view);
		}
	}

	public void setListener(OnClickListener mListener) {
		this.mListener = mListener;
	}

	public void hide() {
		if (mEventListener != null) {
			mEventListener.onShowcaseViewHide(this);
		}
		setVisibility(View.GONE);
	}

	public void show() {
		if (mEventListener != null) {
			mEventListener.onShowcaseViewShow(this);
		}
		setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (!block) {
			return false;
		} else {
			float xDelta = Math.abs(motionEvent.getRawX() - showcaseX);
			float yDelta = Math.abs(motionEvent.getRawY() - showcaseY);
			double distanceFromFocus = Math.sqrt(Math.pow(xDelta, 2)
					+ Math.pow(yDelta, 2));
			return distanceFromFocus > showcaseRadius;
		}
	}

	public interface OnShowcaseEventListener {

		public void onShowcaseViewHide(ShowcaseView showcaseView);

		public void onShowcaseViewShow(ShowcaseView showcaseView);

	}

}
