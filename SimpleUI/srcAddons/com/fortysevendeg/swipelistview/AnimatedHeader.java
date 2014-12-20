package com.fortysevendeg.swipelistview;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;

import com.fortysevendeg.swipelistview.SwipeListViewTouchListener.OnScrollListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnimatedHeader implements OnScrollListener, OnGlobalLayoutListener {
	private static final int STATE_ONSCREEN = 0;
	private static final int STATE_OFFSCREEN = 1;
	private static final int STATE_RETURNING = 2;
	private static final int STATE_EXPANDED = 3;

	private SwipeListViewWithHeader mListView;
	private View mQuickReturnView;
	private View mPlaceHolderInHeaderForQuickReturnView;

	private int mCachedVerticalScrollRange;
	private int mQuickReturnHeight;

	public AnimatedHeader(SwipeListViewWithHeader targetList) {
		this.mListView = targetList;
	}

	public void addHeaderView(View quickReturnView, View headerContainer,
			View placeHolderInHeaderForQuickReturnView) {
		this.mQuickReturnView = quickReturnView;
		this.mPlaceHolderInHeaderForQuickReturnView = placeHolderInHeaderForQuickReturnView;
		mListView.addHeaderView(headerContainer);
		mListView.getViewTreeObserver().addOnGlobalLayoutListener(this);
		mListView.getTouchListener().setOnScrollListener(this);
	}

	@Override
	public void onGlobalLayout() {
		mQuickReturnHeight = mQuickReturnView.getHeight();
		mListView.computeScrollY();
		mCachedVerticalScrollRange = mListView.getListHeight();
	}

	private int mState = STATE_ONSCREEN;
	private int mScrollY;
	private int mMinRawY = 0;
	private int rawY;
	private boolean animationRunning = false;
	private TranslateAnimation anim;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		mScrollY = 0;
		int translationY = 0;

		if (mListView.scrollYIsComputed()) {
			mScrollY = mListView.getComputedScrollY();
		}

		rawY = mPlaceHolderInHeaderForQuickReturnView.getTop()
				- Math.min(mCachedVerticalScrollRange - mListView.getHeight(),
						mScrollY);

		switch (mState) {
		case STATE_OFFSCREEN:
			if (rawY <= mMinRawY) {
				mMinRawY = rawY;
			} else {
				mState = STATE_RETURNING;
			}
			translationY = rawY;
			break;

		case STATE_ONSCREEN:
			if (rawY < -mQuickReturnHeight) {
				mState = STATE_OFFSCREEN;
				mMinRawY = rawY;
			}
			translationY = rawY;
			break;

		case STATE_RETURNING:

			if (translationY > 0) {
				translationY = 0;
				mMinRawY = rawY - mQuickReturnHeight;
			}

			else if (rawY > 0) {
				mState = STATE_ONSCREEN;
				translationY = rawY;
			}

			else if (translationY < -mQuickReturnHeight) {
				mState = STATE_OFFSCREEN;
				mMinRawY = rawY;

			} else if (mQuickReturnView.getTranslationY() != 0
					&& !animationRunning) {
				animationRunning = true;
				anim = new TranslateAnimation(0, 0, -mQuickReturnHeight, 0);
				anim.setFillAfter(true);
				anim.setDuration(250);
				mQuickReturnView.startAnimation(anim);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						animationRunning = false;
						mMinRawY = rawY;
						mState = STATE_EXPANDED;
					}
				});
			}
			break;

		case STATE_EXPANDED:
			if (rawY < mMinRawY - 2 && !animationRunning) {
				animationRunning = true;
				anim = new TranslateAnimation(0, 0, 0, -mQuickReturnHeight);
				anim.setFillAfter(true);
				anim.setDuration(250);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						animationRunning = false;
						mState = STATE_OFFSCREEN;
					}
				});
				mQuickReturnView.startAnimation(anim);
			} else if (translationY > 0) {
				translationY = 0;
				mMinRawY = rawY - mQuickReturnHeight;
			}

			else if (rawY > 0) {
				mState = STATE_ONSCREEN;
				translationY = rawY;
			}

			else if (translationY < -mQuickReturnHeight) {
				mState = STATE_OFFSCREEN;
				mMinRawY = rawY;
			} else {
				mMinRawY = rawY;
			}
		}
		/** this can be used if the build is below honeycomb **/
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
			anim = new TranslateAnimation(0, 0, translationY, translationY);
			anim.setFillAfter(true);
			anim.setDuration(0);
			mQuickReturnView.startAnimation(anim);
		} else {
			mQuickReturnView.setTranslationY(translationY);
		}

	}

}
