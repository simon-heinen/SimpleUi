package simpleui.modifiers.v3;

import java.util.ArrayList;
import java.util.List;

import simpleui.adapters.SimpleBaseAdapter;
import simpleui.adapters.SimpleBaseAdapter.HasItsOwnView;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.M_ListWrapperV2;
import simpleui.util.DeviceInformation;
import simpleui.util.Log;
import simpleui.util.StickyListHeader;
import simpleui.util.ToastV2;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewWithHeader;

/**
 * not yet usable, use {@link M_ListWrapperV2}
 * 
 * - add move feature if easily possible
 * 
 * - make edit features optional: delete, move,
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

	private final List<T> targetCollection;
	private List<T> copyOfTargetCollection;
	private final boolean instantModelUpdates;
	private Integer listViewHeight = null;

	private final int backViewId;

	private final int frontViewId;

	private View headerContent;

	private View stickyFloatingBox;

	private SimpleBaseAdapter adapter;

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
			boolean instantModelUpdates, int frontViewId, int backViewId,
			View headerContent, View stickyFloatingBox) {
		this.targetCollection = targetCollection;
		this.instantModelUpdates = instantModelUpdates;
		this.backViewId = backViewId;
		this.frontViewId = frontViewId;
		this.headerContent = headerContent;
		this.stickyFloatingBox = stickyFloatingBox;
	}

	public M_ListWrapperV4Editable(List<T> targetCollection,
			boolean instantModelUpdates, int frontViewId, int backViewId) {
		this(targetCollection, instantModelUpdates, frontViewId, backViewId,
				null, null);
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
			boolean instantModelUpdates, int frontViewId, int backViewId,
			int listViewHeight, View headerContent, View stickyFloatingBox) {
		this(targetCollection, instantModelUpdates, frontViewId, backViewId,
				headerContent, stickyFloatingBox);
		this.listViewHeight = listViewHeight;
	}

	@Override
	public View getView(final Context context) {

		if (instantModelUpdates) {
			copyOfTargetCollection = targetCollection;
		} else {
			copyOfTargetCollection = new ArrayList<T>(targetCollection);
		}
		adapter = new SimpleBaseAdapter(null, copyOfTargetCollection);

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
				if (mAdapter == null || mAdapter.getCount() == 0) {
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
					if (onRemoveRequest(deletedItem)) {
						deleteItemFromModel(context, deletedItem,
								itemPosToDelete);
					}
				}
				notifyAdapterThatDataChanged();
			}

			private void deleteItemFromModel(final Context context,
					final T deletedItem, final int itemPosToDelete) {
				String infoText = "Deleted entry nr. " + itemPosToDelete;
				if (hasImplementedToString(deletedItem)) {
					infoText = "Deleted " + deletedItem;
				}
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

		// TODO pass headerCOntent as parameter instead of hardcoding it here:

		FrameLayout wrapper = new FrameLayout(context);
		wrapper.addView(listView);
		if (headerContent != null && stickyFloatingBox != null) {
			attachStickyHeader(context, listView, headerContent,
					stickyFloatingBox);
			wrapper.addView(stickyFloatingBox);
		}
		return wrapper;
	}

	private void attachStickyHeader(final Context context,
			final SwipeListViewWithHeader listView, View headerContent,
			final View stickyFloatingBox) {
		LinearLayout headerContainer = new LinearLayout(context);
		headerContainer.setOrientation(LinearLayout.VERTICAL);
		headerContainer.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		final View placeHolderBelowFloatingBox = newPlaceHolderFor(context,
				stickyFloatingBox);
		headerContainer.addView(headerContent);
		headerContainer.addView(placeHolderBelowFloatingBox);

		StickyListHeader animatedHeader = new StickyListHeader(listView,
				stickyFloatingBox, placeHolderBelowFloatingBox);
		listView.addHeaderView(headerContainer);
		listView.getViewTreeObserver()
				.addOnGlobalLayoutListener(animatedHeader);
		listView.getTouchListener().setOnScrollListener(animatedHeader);
	}

	/**
	 * generates a transparent placeholder which will have the same hight as the
	 * targetView
	 * 
	 * @param context
	 * @param targetView
	 * @return
	 */
	private View newPlaceHolderFor(final Context context, final View targetView) {
		final View placeHolderBelowFloatingBox = newInvisibleContainer(context,
				targetView.getLayoutParams().height + 1); // init rnd height
		targetView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					// use the same height as soon as it is known:
					@Override
					public void onGlobalLayout() {
						LayoutParams p = placeHolderBelowFloatingBox
								.getLayoutParams();
						p.height = targetView.getHeight();
						placeHolderBelowFloatingBox.setLayoutParams(p);
						targetView.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		return placeHolderBelowFloatingBox;
	}

	private LinearLayout newInvisibleContainer(Context context, int height) {
		LinearLayout block = new LinearLayout(context);
		block.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, height));
		return block;
	}

	public boolean addListElement(Integer location, T newElement) {
		if (location != null) {
			copyOfTargetCollection.add(location, newElement);
		} else {
			copyOfTargetCollection.add(newElement);
		}
		adapter.notifyDataSetChanged();
		return true;
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
	 * will be called before the modifier for the item could save its content
	 * 
	 * @param item
	 * @return false if the item was not removed and its modifier view should
	 *         reapear
	 */
	public abstract boolean onRemoveRequest(T item);

}
