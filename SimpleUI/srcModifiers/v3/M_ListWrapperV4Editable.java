package v3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tools.DeviceInformation;
import tools.ToastV2;
import util.Log;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.util.ColorUtils;
import adapters.SimpleBaseAdapter;
import adapters.SimpleBaseAdapter.HasItsOwnView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.fortysevendeg.swipelistview.AnimatedHeader;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewWithHeader;

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

	private final int backViewId;

	private final int frontViewId;

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
		this.backViewId = backViewId;
		this.frontViewId = frontViewId;
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
	public View getView(final Context context) {

		if (instantModelUpdates) {
			copyOfTargetCollection = targetCollection;
		} else {
			copyOfTargetCollection = new ArrayList<T>(targetCollection);
		}
		final SimpleBaseAdapter adapter = new SimpleBaseAdapter(null,
				copyOfTargetCollection);

		final SwipeListViewWithHeader listView = new SwipeListViewWithHeader(
				context, backViewId, frontViewId) {

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

		listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onClickFrontView(View frontView, int position) {
				int pos = position - listView.getHeaderViewsCount();
				copyOfTargetCollection.get(pos).onItemClick(frontView, pos);
			}

			@Override
			public boolean onLongClickFrontView(View frontView, int position) {
				int pos = position - listView.getHeaderViewsCount();
				return copyOfTargetCollection.get(pos).onItemLongClick(
						frontView, pos);
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {

				for (int i : reverseSortedPositions) {
					final int itemPosToDelete = i
							- listView.getHeaderViewsCount();
					Log.i(LOG_TAG, "itemIdsToDelete=" + itemPosToDelete);
					final T deletedItem = copyOfTargetCollection
							.remove(itemPosToDelete);
					String infoText = "Deleted entry nr. " + itemPosToDelete;
					if (hasImplementedToString(deletedItem))
						infoText = "Deleted " + deletedItem;
					ToastV2.showUndoToast(context, infoText, "Undo",
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									copyOfTargetCollection.add(itemPosToDelete,
											deletedItem);
									notifyAdapterThatDataChanged();
								}
							});
				}
				notifyAdapterThatDataChanged();
			}

			private boolean hasImplementedToString(Object o) {
				try {
					return (o.getClass().getMethod("toString")
							.getDeclaringClass() != Object.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			private void notifyAdapterThatDataChanged() {
				adapter.notifyDataSetChanged();
			}

		});
		listView.setSwipeOpenOnLongPress(false);
		listView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);
		listView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
		if (listViewHeight != null) {
			LayoutParams p2 = listView.getLayoutParams();
			p2.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			p2.height = listViewHeight;
			listView.setLayoutParams(p2);
		}
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter.newOnClickListener());
		// l.setOnItemLongClickListener(b.newOnLongClickListener());

		LinearLayout headerContainer = new LinearLayout(context);
		headerContainer.setOrientation(LinearLayout.VERTICAL);
		headerContainer.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		headerContainer.setBackgroundColor(ColorUtils.RED);

		FrameLayout wrapper = new FrameLayout(context);
		wrapper.addView(listView);
		
		// TODO pass headerCOntent as parameter instead of hardcoding it here:
		LinearLayout headerContent = newBox(context, 400);

		headerContainer.addView(headerContent);

		/*
		 * TODO calc height of sticky floating box and placeHolder dynamically
		 * and not hardcoded,
		 */
		int heightOfStickyHeader = 150;
		View placeHolderBelowFloatingBox = newBox(context, heightOfStickyHeader);
		LinearLayout stickyFloatingBox = newBox(context, heightOfStickyHeader);
		stickyFloatingBox.setBackgroundColor(ColorUtils.randomColor());

		headerContainer.addView(placeHolderBelowFloatingBox);

		AnimatedHeader animatedHeader = new AnimatedHeader(listView,
				stickyFloatingBox, placeHolderBelowFloatingBox);
		listView.addHeaderView(headerContainer);
		listView.getViewTreeObserver()
				.addOnGlobalLayoutListener(animatedHeader);
		listView.getTouchListener().setOnScrollListener(animatedHeader);


		wrapper.addView(stickyFloatingBox);
		return wrapper;
	}

	private LinearLayout newBox(Context context, int height) {
		LinearLayout block = new LinearLayout(context);
		block.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, height));
		return block;
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
