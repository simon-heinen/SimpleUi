package simpleui.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ListView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StickyListHeader implements OnScrollListener,
		OnGlobalLayoutListener {
	private static final String LOG_TAG = StickyListHeader.class
			.getSimpleName();
	private static final int STATE_ONSCREEN = 0;
	private static final int STATE_OFFSCREEN = 1;
	private static final int STATE_RETURNING = 2;
	private static final int STATE_EXPANDED = 3;
	private static int mItemCount;

	private final AbsListView mListView;
	private final View mQuickReturnView;
	private final View mPlaceHolderInHeaderForQuickReturnView;

	private int mCachedVerticalScrollRange;
	private int mQuickReturnHeight;

	/**
	 * After you created the {@link StickyListHeader} you need to attach it to
	 * the {@link ListView} as a {@link OnScrollListener} and as an
	 * {@link OnGlobalLayoutListener}. Then you use
	 * {@link ListView#addHeaderView(View)} to add the placeholder (add it
	 * wrapped in a container so that you can add another view like an image or
	 * caption above the placeholder). The quickReturnView itself has to be
	 * added to a {@link FrameLayout} which also contains the {@link ListView},
	 * so it is not part of the {@link ListView} itself (and thats why the
	 * placeholder is needed)
	 * 
	 * @param targetList
	 *            the listview where the sticky header should be applied to
	 * @param quickReturnView
	 *            the content shown in the sticky header
	 * @param placeHolderInHeaderForQuickReturnView
	 *            has to have the same height as the quickReturnView
	 */
	public StickyListHeader(AbsListView targetList, View quickReturnView,
			View placeHolderInHeaderForQuickReturnView) {
		this.mListView = targetList;
		this.mQuickReturnView = quickReturnView;
		this.mPlaceHolderInHeaderForQuickReturnView = placeHolderInHeaderForQuickReturnView;
	}

	@Override
	public void onGlobalLayout() {
		mQuickReturnHeight = mQuickReturnView.getHeight();
		computeScrollY(mListView);
		mCachedVerticalScrollRange = mListViewHeight;
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

		if (scrollIsComputed) {
			mScrollY = getComputedScrollY(mListView);
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

	private int mItemOffsetY[];
	private boolean scrollIsComputed = false;
	private int mListViewHeight;

	public void computeScrollY(AbsListView listView) {
		mItemOffsetY = new int[listView.getAdapter().getCount()];
		mListViewHeight = calcHeightPlusOffsets(listView, mItemOffsetY,
				mListViewHeight);
		scrollIsComputed = true;
	}

	private static int calcHeightPlusOffsets(AbsListView listView,
			int[] itemOffsetList, int oldHeight) {
		Log.d(LOG_TAG, "calcHeightPlusOffsets called, oldHeight=" + oldHeight);
		if (mItemCount == listView.getAdapter().getCount()) {
			return oldHeight;
		}
		mItemCount = listView.getAdapter().getCount();
		int heightSum = 0;
		for (int i = 0; i < mItemCount; ++i) {
			View view = listView.getAdapter().getView(i, null, listView);
			view.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			itemOffsetList[i] = heightSum;
			heightSum += view.getMeasuredHeight();
		}
		Log.d(LOG_TAG, "   > calcHeightPlusOffsets done, mHeight=" + heightSum);
		return heightSum;
	}

	public int getComputedScrollY(AbsListView l) {
		return mItemOffsetY[l.getFirstVisiblePosition()]
				- l.getChildAt(0).getTop();
	}

	@Override
	public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
	}

}
