package simpleui.modifiers.v3;

import java.util.List;

import simpleui.adapters.SimpleBaseAdapter.HasItsOwnView;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.M_ListWrapperV3Editable;
import simpleui.util.Log;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comcast.freeflow.core.AbsLayoutContainer;
import com.comcast.freeflow.core.AbsLayoutContainer.OnItemClickListener;
import com.comcast.freeflow.core.AbsLayoutContainer.OnItemLongClickListener;
import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.HGridLayout;
import com.comcast.freeflow.layouts.VGridLayout;

/**
 * This version of the listwrapper will only show unmodifiable content, if the
 * user should be able to add, move and remove elements, use
 * {@link M_ListWrapperV3Editable}
 *
 * @param <T>
 */
public class M_ListWrapperV3<T extends HasItsOwnView> implements
		ModifierInterface {

	protected static final String LOG_TAG = M_ListWrapperV3.class
			.getSimpleName();
	private final List<T> targetList;
	private final int listHeight;
	private final int initialRowOrColumnCount;
	private final boolean startInVerticalMode;
	private FreeFlowContainer container;

	public M_ListWrapperV3(List<T> targetList, int listHeight,
			int nrOfParallelGridElements, boolean startInVerticalMode) {
		this.targetList = targetList;
		this.listHeight = listHeight;
		this.initialRowOrColumnCount = nrOfParallelGridElements;
		this.startInVerticalMode = startInVerticalMode;
	}

	@Override
	public View getView(final Context c) {

		container = new FreeFlowContainer(c) {

			@Override
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				Log.d(LOG_TAG, "onMeasure called");
				Log.d(LOG_TAG, "  > widthMeasureSpec=" + widthMeasureSpec);
				Log.d(LOG_TAG, "  > heightMeasureSpec=" + heightMeasureSpec);
				if (heightMeasureSpec <= 0) {
					// To avoid bug in some layouts
					heightMeasureSpec = widthMeasureSpec;
					android.view.ViewGroup.LayoutParams p = getLayoutParams();
					p.height = listHeight;
					setLayoutParams(p);
				}
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}

			@Override
			protected void onSizeChanged(int w, int h, int oldw, int oldh) {
				Log.d(LOG_TAG, "onSizeChanged, oldw=" + oldw);
				if (oldw <= 0) { // the first time the view is resized:
					if (startInVerticalMode) {
						switchToVerticalGrid(initialRowOrColumnCount);
					} else {
						switchToHorizontalGrid(initialRowOrColumnCount);
					}
				} else {
					// now the correct size is known, so force the currently
					// visible children to update their layout
					// int size = getChildCount();
					// Log.d(LOG_TAG, "Updating " + size + " children");
					// for (int i = 0; i < size; i++) {
					// View child = getChildAt(i);
					// child.requestLayout();
					// child.invalidate();
					// }
				}
				super.onSizeChanged(w, h, oldw, oldh);
			}

		};
		container.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO add logic if list view is scrolled down/up completely
				// that then requestDisallowInterceptTouchEvent(false) is
				// passed (check event if up or down scroll event):
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		container.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AbsLayoutContainer parent,
					FreeFlowItem proxy) {
				((HasItsOwnView) proxy.data).onItemClick(proxy.view,
						proxy.itemIndex);
			}

		});
		// hack to fix https://github.com/Comcast/FreeFlow/pull/73/files :
		container.setChoiceMode(FreeFlowContainer.CHOICE_MODE_SINGLE);
		container.setChoiceMode(FreeFlowContainer.CHOICE_MODE_NONE);
		container.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AbsLayoutContainer parent,
					View view, int sectionIndex, int positionInSection, long id) {
				return targetList.get(positionInSection).onItemLongClick(view,
						positionInSection);
			}
		});
		SectionedAdapter wrappingAdapter = new SectionedAdapter() {

			@Override
			public boolean shouldDisplaySectionHeaders() {
				return false;
			}

			@Override
			public Class[] getViewTypes() {
				return new Class[] { TextView.class };
			}

			@Override
			public Class getViewType(FreeFlowItem proxy) {
				return TextView.class;
			}

			@Override
			public Section getSection(int index) {
				Section section = new Section();
				section.setData((List<Object>) targetList);
				return section;
			}

			@Override
			public int getNumberOfSections() {
				return 1;
			}

			@Override
			public View getItemView(int section, int position,
					View convertView, ViewGroup parent) {
				return targetList.get(position).getView(c, convertView, parent,
						null, targetList, position);
			}

			@Override
			public long getItemId(int section, int position) {
				return position;
			}

			@Override
			public View getHeaderViewForSection(int section, View convertView,
					ViewGroup parent) {
				throw new RuntimeException(
						"getHeaderViewForSection(int section, View convertView, ViewGroup parent)");
			}
		};
		container.setAdapter(wrappingAdapter);
		return container;
	}

	public boolean switchToVerticalGrid(int columnCount) {
		if (container != null) {
			final int itemWidth = container.getWidth() / columnCount;
			final int itemHeight = itemWidth;
			FreeFlowLayout vLayout = new VGridLayout();
			vLayout.setLayoutParams(new VGridLayout.LayoutParams(itemWidth,
					itemHeight, itemWidth, itemHeight));
			container.setLayout(vLayout);
			return true;
		}
		return false;
	}

	public boolean switchToHorizontalGrid(int rowCount) {
		if (container != null) {
			final int itemHeight = container.getHeight() / rowCount;
			final int itemWidth = itemHeight;
			FreeFlowLayout vLayout = new HGridLayout();
			vLayout.setLayoutParams(new HGridLayout.LayoutParams(itemWidth,
					itemHeight, itemWidth, itemHeight));
			container.setLayout(vLayout);
			return true;
		}
		return false;
	}

	@Override
	public boolean save() {
		boolean result = true;
		for (T t : targetList) {
			if (t instanceof ModifierInterface) {
				result &= ((ModifierInterface) t).save();
			}
		}
		return result;
	}

}
