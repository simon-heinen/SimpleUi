package fragments;

import com.bitstars.uitemplate.DefaultSimpleMenuAdapter;
import com.bitstars.uitemplate.R;
import com.bitstars.uitemplate.R.id;
import com.bitstars.uitemplate.R.layout;
import com.bitstars.uitemplate.R.menu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DefaultLeftNavbar extends NavBarFragment {
	private static final String LOG_TAG = "DefaultLeftNavbar";

	@Override
	public DefaultSimpleMenuAdapter newListAdapter() {
		return new DefaultSimpleMenuAdapter(getActivity(),
				R.menu.default_left_navbar);
	}

	@Override
	public void onMenuEntryClicked(Object object) {
		MenuItem item = (MenuItem) object;
		if (item.getItemId() == R.id.settings) {
			Log.e(LOG_TAG, "Settings clicked");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.default_navbar, null);
		Button b = (Button) v.findViewById(R.id.navbar_button);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e(LOG_TAG, "button clicked");
			}
		});
		return v;
	}
}
