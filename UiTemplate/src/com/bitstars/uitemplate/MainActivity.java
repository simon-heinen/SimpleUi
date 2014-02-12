package com.bitstars.uitemplate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fragments.ListViewDraggingAnimation;
import fragments.MainPage;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
		SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		pagerAdapter.addPage("Test", new MainPage());
		pagerAdapter.addPage("Test2", new ListViewDraggingAnimation());
		mViewPager.setAdapter(pagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// add the menu in the action bar:
		getMenuInflater().inflate(R.menu.actionbar_menu, menu);

		// TODO
		// MenuItem searchItem = menu.findItem(R.id.actionbar_search);
		// SearchView mSearchView = (SearchView) MenuItemCompat
		// .getActionView(searchItem);
		// mSearchView...

		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
