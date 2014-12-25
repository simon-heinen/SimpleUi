package simpleui.util.tooltips;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TooltipControl {

	private static final String LOG_TAG = "TooltipControl";
	private final ViewGroup targetContainer;
	private final RelativeLayout overlayContainer;
	private int bubblePadding = 40;
	private int bubbleCornerRadiusInPixel = 20;
	private Integer bubbleColor = Color.WHITE;
	private int overlayColor = Color.argb(100, 0, 0, 0);

	private int bubbleOffset;
	private final Handler mHandler = new Handler(Looper.getMainLooper());

	public TooltipControl(ViewGroup targetContainer) {
		this.targetContainer = targetContainer;
		this.overlayContainer = new RelativeLayout(targetContainer.getContext());
	}

	/**
	 * @param viewToHighlight
	 * @param sizeFactor
	 *            see {@link HighlightDrawable#setSizeFactor(float)}
	 * @return
	 */
	public HighlightDrawable setHighlightArea(View viewToHighlight,
			float sizeFactor) {
		final HighlightDrawable drawable = setHighlightArea(viewToHighlight);
		drawable.setSizeFactor(sizeFactor);
		return drawable;
	}

	public HighlightDrawable setHighlightArea(View viewToHighlight) {
		targetContainer.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int l, int t, int r, int b,
					int ol, int ot, int or, int ob) {
				addOverlayContainer();
			}
		});

		final HighlightDrawable drawable = new HighlightDrawable(
				targetContainer, overlayColor);
		overlayContainer.setBackgroundDrawable(drawable);
		drawable.setHighlightArea(viewToHighlight);
		return drawable;
	}

	/**
	 * @param targetView
	 * @param viewToAdd
	 * @param centerOntopView
	 */
	public void setOverlayViewCenteredTo(View targetView, View viewToAdd,
			boolean centerOntopView) {
		setOverlayViewCenteredTo(targetView, viewToAdd, false, true,
				centerOntopView);
	}

	public void setViewCenteredTo(final View targetView, final View viewToAdd,
			final boolean belowTargetView, boolean centerOntopView) {
		setOverlayViewCenteredTo(targetView, viewToAdd, belowTargetView, false,
				centerOntopView);
	}

	/**
	 * @param targetView
	 * @param viewToAdd
	 * @param belowTargetView
	 *            ignored if autoCalcTopOrBelow is set to true
	 * @param autoCalcTopOrBelow
	 * @param centerOntopView
	 *            if true the overlaid view will be not below the target view
	 *            but ontop instead (useful for large views like images e.g.
	 */
	private void setOverlayViewCenteredTo(final View targetView,
			final View viewToAdd, final boolean belowTargetView,
			final boolean autoCalcTopOrBelow, final boolean centerOntopView) {
		removeBubble();

		if (targetView == null) {
			Log.e(LOG_TAG, "passed target view to add tooltip to was null!");
			return;
		}

		overlayContainer.addView(viewToAdd);
		viewToAdd.setPadding(bubblePadding, bubblePadding, bubblePadding,
				bubblePadding);
		viewToAdd.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int l, int to, int r, int b,
					int ol, int ot, int or, int ob) {
				viewToAdd.removeOnLayoutChangeListener(this);
				overlayContainer.setAlpha(0f);
				addOverlayContainer();
				calcViewPos(targetView, viewToAdd, belowTargetView,
						autoCalcTopOrBelow, centerOntopView);

				final float oldtrans = viewToAdd.getTranslationY();
				viewToAdd.setTranslationY(600);

				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						overlayContainer.animate().setDuration(300).alpha(1f)
								.start();
						viewToAdd.animate().setDuration(300)
								.translationY(oldtrans).start();
					}
				}, 100);

			}
		});
	}

	private void calcViewPos(View targetView, View v, boolean belowTargetView,
			boolean autoCalcTopOrBelow, boolean centerOntopView) {
		int[] viewPos = new int[2];
		int[] parentPos = new int[2];
		targetView.getLocationOnScreen(viewPos);
		targetContainer.getLocationOnScreen(parentPos);
		int arrowAlignment = 0;
		int leftPosX = 0;
		int tvWCenter = viewPos[0] - parentPos[0] + targetView.getWidth() / 2;
		if (v.getWidth() < targetContainer.getWidth()) {
			leftPosX = tvWCenter - v.getWidth() / 2;
			// arrow must be on the center
			if (leftPosX < 0) {
				leftPosX = 0;

				// arrow must be on the left top side
				arrowAlignment = 1;
			} else if (leftPosX > targetContainer.getWidth() - v.getWidth()) {
				leftPosX = targetContainer.getWidth() - v.getWidth();

				// arrow must be on the right top side
				arrowAlignment = 2;
			}
		} else {
			// overlay uses max width, check if tvWCenter is more left or right
			if (tvWCenter < targetContainer.getWidth() * 0.3f) {
				arrowAlignment = 1;
			}
			if (tvWCenter > targetContainer.getWidth() * 0.7f) {
				arrowAlignment = 2;
			}
		}
		int topPosY = viewPos[1] - parentPos[1];
		if (autoCalcTopOrBelow) {
			belowTargetView = topPosY < targetContainer.getHeight() / 2;
		}
		int distanceFromCenter = 0;
		if (belowTargetView) {
			distanceFromCenter += targetView.getHeight();
			if (centerOntopView) {
				distanceFromCenter *= 0.6f;
			}
			distanceFromCenter += bubbleOffset;
		} else {
			distanceFromCenter += -v.getHeight();
			if (centerOntopView) {
				distanceFromCenter += targetView.getHeight() * 0.4f;
			}
			distanceFromCenter -= bubbleOffset;
		}
		topPosY += distanceFromCenter;
		v.setX(leftPosX);
		v.setY(topPosY);
		if (bubbleColor != null) {
			boolean arrowOnTop = belowTargetView;
			v.setBackgroundDrawable(new ChatBubbleDrawable(arrowAlignment,
					arrowOnTop, bubbleColor, bubblePadding,
					bubbleCornerRadiusInPixel));
		}
	}

	/**
	 * @param bubbleOffsetInPixels
	 *            to place the overlay view not directly ontop of the target
	 *            view but instead a little below or above use this one
	 */
	public void setBubbleOffset(int bubbleOffsetInPixels) {
		this.bubbleOffset = bubbleOffsetInPixels;
	}

	private boolean valuesUnchanged(
			android.view.ViewGroup.LayoutParams oldParams, LayoutParams newP) {
		if (oldParams instanceof RelativeLayout.LayoutParams) {
			LayoutParams oldP = (LayoutParams) oldParams;
			return oldP.width == newP.width && oldP.height == newP.height
					&& oldP.leftMargin == newP.leftMargin
					&& oldP.topMargin == newP.topMargin;
		}
		return false;
	}

	public void setOverlayColor(int overlayColor) {
		this.overlayColor = overlayColor;
	}

	/**
	 * sets the color of the background (will be a {@link HighlightDrawable})
	 * 
	 * @param bubbleColor
	 *            if null no background bubble will be created!
	 */
	public void setBubbleColor(Integer bubbleColor) {
		this.bubbleColor = bubbleColor;
	}

	public void setBubbleCornerRadiusInPixel(int bubbleCornerRadiusInPixel) {
		this.bubbleCornerRadiusInPixel = bubbleCornerRadiusInPixel;
	}

	public void setBubblePadding(int bubblePadding) {
		this.bubblePadding = bubblePadding;
	}

	private void addOverlayContainer() {
		LayoutParams params = new LayoutParams(targetContainer.getWidth(),
				targetContainer.getHeight());
		if (overlayContainer.getParent() == null) {
			targetContainer.addView(overlayContainer, params);
		} else if (!valuesUnchanged(overlayContainer.getLayoutParams(), params)) {
			overlayContainer.setLayoutParams(params);
		}

	}

	public void removeOverlay() {
		removeBubble();
		overlayContainer.setBackgroundDrawable(null);
	}

	private void removeBubble() {
		// TODO add fade out animation
		overlayContainer.removeAllViews();
	}
}
