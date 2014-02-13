package fragments;

import adapters.SimpleMenuAdapter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * needs a layout with list view in it which has the id @android:id/list:
 * 
 * ListView android:id="@android:id/list"...
 * 
 */
public abstract class NavBarFragment extends ListFragment {

	private BaseAdapter mNavigationAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mNavigationAdapter = newListAdapter();
		setListAdapter(mNavigationAdapter);
	}

	public abstract BaseAdapter newListAdapter();

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		onMenuEntryClicked(mNavigationAdapter.getItem(position));
	}

	/**
	 * @param item
	 *            the type depends on the adapter you passed in
	 *            {@link NavBarFragment#newListAdapter()}. E.g. if you passed a
	 *            {@link SimpleMenuAdapter} it will be a {@link MenuItem}
	 */
	public abstract void onMenuEntryClicked(Object item);

}
