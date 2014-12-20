package com.fortysevendeg.swipelistview;

import android.content.Context;
import android.view.View;

public class SwipeListViewWithHeader extends SwipeListView {

	private int mItemCount;
	private int mItemOffsetY[];
	private boolean scrollIsComputed = false;
	private int mHeight;

	public SwipeListViewWithHeader(Context context, int swipeBackView,
			int swipeFrontView) {
		super(context, swipeBackView, swipeFrontView);
	}

	public void computeScrollY() {
		mHeight = 0;
		mItemCount = getAdapter().getCount();
		if (mItemOffsetY == null) {
			mItemOffsetY = new int[mItemCount];
		}
		for (int i = 0; i < mItemCount; ++i) {
			View view = getAdapter().getView(i, null, this);
			view.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			mItemOffsetY[i] = mHeight;
			mHeight += view.getMeasuredHeight();
		}
		scrollIsComputed = true;
	}

	public boolean scrollYIsComputed() {
		return scrollIsComputed;
	}

	public int getListHeight() {
		return mHeight;
	}

	public int getComputedScrollY() {
		return mItemOffsetY[getFirstVisiblePosition()] - getChildAt(0).getTop();
	}
}
