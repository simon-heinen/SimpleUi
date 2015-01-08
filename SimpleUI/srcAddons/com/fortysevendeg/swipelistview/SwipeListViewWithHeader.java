package com.fortysevendeg.swipelistview;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;

// TODO extract interface for other listviews which also want to use the header concept
public class SwipeListViewWithHeader extends SwipeListView {


	public SwipeListViewWithHeader(Context context, int swipeBackView,
			int swipeFrontView) {
		super(context, swipeBackView, swipeFrontView);
	}


}
