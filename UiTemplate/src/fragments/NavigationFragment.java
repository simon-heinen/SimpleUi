package fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bitstars.uitemplate.R;

/**
 * Created with IntelliJ IDEA. User: maui Date: 05.11.13 Time: 01:36 To change
 * this template use File | Settings | File Templates.
 */
public class NavigationFragment extends ListFragment {

	private MenuBaseAdapter mNavigationAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mNavigationAdapter = new MenuBaseAdapter(getActivity(),
				R.menu.actionbar_menu);
		setListAdapter(mNavigationAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		MenuItem item = mNavigationAdapter.getItem(position);

		// if (item.getItemId() == R.id.settings) {
		// Intent settingsIntent = new Intent(getActivity(),
		// SettingsActivity.class);
		// startActivity(settingsIntent);
		// }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_navigation, null);
	}
}
