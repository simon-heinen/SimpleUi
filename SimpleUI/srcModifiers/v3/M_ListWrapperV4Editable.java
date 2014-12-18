package v3;

import java.util.ArrayList;
import java.util.List;

import tools.DeviceInformation;
import util.Log;
import v2.simpleUi.ModifierInterface;
import adapters.SimpleBaseAdapter;
import adapters.SimpleBaseAdapter.HasItsOwnView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.fortysevendeg.swipelistview.SwipeListView;

/**
 * not yet usable, use {@link M_ListWrapperV2}
 * 
 */
@Deprecated
public abstract class M_ListWrapperV4Editable<T extends HasItsOwnView>
		implements ModifierInterface {

	private static final String LOG_TAG = M_ListWrapperV4Editable.class
			.getSimpleName();

	/**
	 * if the listview is child of a scrollview the height has to be calculated
	 * and set to a fixed value, the max height of the list in the scrollview
	 * will be MAX_HEIGHT_PERCENTAGE_IN_CONTAINER * screenheight
	 */
	private static final float MAX_HEIGHT_PERCENTAGE_IN_CONTAINER = 0.75f;

	private final String textForAddButton;
	private final List<T> targetCollection;
	private List<T> copyOfTargetCollection;
	private final boolean instantModelUpdates;
	private Integer listViewHeight = null;

	private final int frontViewId;

	private final int backViewId;

	/**
	 * @param targetCollection
	 * @param textForAddButton
	 * @param instantModelUpdates
	 *            if true the passed list will be updated directly and changes
	 *            can't be aborted. if false the original passed list will only
	 *            be modified when the {@link ModifierInterface#save()} method
	 *            is called
	 */
	public M_ListWrapperV4Editable(List<T> targetCollection,
			String textForAddButton, boolean instantModelUpdates,
			int frontViewId, int backViewId) {
		this.targetCollection = targetCollection;
		this.textForAddButton = textForAddButton;
		this.instantModelUpdates = instantModelUpdates;
		this.frontViewId = frontViewId;
		this.backViewId = backViewId;
	}

	/**
	 * @param targetCollection
	 * @param textForAddButton
	 * @param instantModelUpdates
	 *            if true the passed list will be updated directly and changes
	 *            can't be aborted. if false the original passed list will only
	 *            be modified when the {@link ModifierInterface#save()} method
	 *            is called
	 */
	public M_ListWrapperV4Editable(List<T> targetCollection,
			String textForAddButton, boolean instantModelUpdates,
			int frontViewId, int backViewId, int listViewHeight) {
		this(targetCollection, textForAddButton, instantModelUpdates,
				frontViewId, backViewId);
		this.listViewHeight = listViewHeight;
	}

	@Override
	public View getView(Context context) {

		if (instantModelUpdates) {
			copyOfTargetCollection = targetCollection;
		} else {
			copyOfTargetCollection = new ArrayList<T>(targetCollection);
		}
		SimpleBaseAdapter b = new SimpleBaseAdapter(null,
				copyOfTargetCollection);

		SwipeListView l = new SwipeListView(context, frontViewId, backViewId) {

			@Override
			protected void onSizeChanged(int w, int requestedViewHeight,
					int oldw, int oldh) {
				if (listViewHeight == null) {
					Log.d(LOG_TAG, "onSizeChanged called");
					Log.d(LOG_TAG, "  > oldw=" + oldw);
					Log.d(LOG_TAG, "  > oldh=" + oldh);
					Log.d(LOG_TAG, "  > w=" + w);
					Log.d(LOG_TAG, "  > h=" + requestedViewHeight);
					Point screenSize = DeviceInformation
							.getScreenSize((Activity) getContext());
					int screenHeight = (int) (screenSize.y * MAX_HEIGHT_PERCENTAGE_IN_CONTAINER);
					if (requestedViewHeight < screenHeight) {
						int listviewHeight = getHeightApproximationForListView();
						Log.d(LOG_TAG, "  > maxListViewHeight=" + screenHeight);
						Log.d(LOG_TAG, "  > listviewHeight=" + listviewHeight);
						listViewHeight = listviewHeight < screenHeight ? listviewHeight
								: screenHeight;
						android.view.ViewGroup.LayoutParams p = getLayoutParams();
						p.height = listViewHeight;
						setLayoutParams(p);
					}
				}
				super.onSizeChanged(w, requestedViewHeight, oldw, oldh);
			}

			private int getHeightApproximationForListView() {
				ListAdapter mAdapter = getAdapter();
				if (mAdapter.getCount() == 0) {
					return 0;
				}
				View mView = mAdapter.getView(0, null, this);
				mView.measure(
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				return mAdapter.getCount() * mView.getMeasuredHeight();
			}

		};

		if (listViewHeight != null) {
			l.setLayoutParams(new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					listViewHeight));
		}
		l.setAdapter(b);
		l.setOnItemClickListener(b.newOnClickListener());
		l.setOnItemLongClickListener(b.newOnLongClickListener());
		l.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		return l;
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (T t : targetCollection) {
			if (t instanceof ModifierInterface) {
				result &= ((ModifierInterface) t).save();
			}
		}
		if (result) {
			if (!instantModelUpdates) {
				targetCollection.clear();
				for (T t : copyOfTargetCollection) {
					targetCollection.add(t);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * @param c
	 * @param nrForNewItem
	 *            can be used for initializing the object e.g. if the type is
	 *            String you could return "Item Nr. "+posOfNewItemInList
	 * @return a new initialized instance of the object
	 */
	public abstract T getNewItemInstance(Context c, int nrForNewItem);

	/**
	 * will be called before the modifier for the item could save its content
	 * 
	 * @param item
	 * @return false if the item was not removed and its modifier view should
	 *         reapear
	 */
	public abstract boolean onRemoveRequest(T item);

	/**
	 * will be executed after the modifier for the new item saved its content so
	 * just add it to your collection when this event appears
	 * 
	 * @param item
	 * @return
	 */
	public abstract boolean onAddRequest(T item);

}
