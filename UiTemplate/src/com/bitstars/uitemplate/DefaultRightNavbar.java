package com.bitstars.uitemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import fragments.NavBarFragment;

public class DefaultRightNavbar extends NavBarFragment {
	private static final String LOG_TAG = "DefaultLeftNavbar";

	@Override
	public DefaultMenuAdapter newListAdapter() {
		return new DefaultMenuAdapter(getActivity(),
				R.menu.default_right_navbar);
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
		View v = inflater.inflate(R.layout.fragment_navigation, null);
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
