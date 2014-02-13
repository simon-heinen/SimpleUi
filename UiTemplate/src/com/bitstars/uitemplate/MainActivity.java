package com.bitstars.uitemplate;

import adapters.SimpleViewPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import fragments.DefaultLeftNavbar;
import fragments.ExampleDraggableListViewFragment;
import fragments.MainFragment;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
		SimpleViewPagerAdapter pagerAdapter = new SimpleViewPagerAdapter(
				getSupportFragmentManager());
		pagerAdapter.addPage("MainFragment", new MainFragment());
		pagerAdapter.addPage("ExampleDraggableListViewFragment",
				new ExampleDraggableListViewFragment());
		pagerAdapter.addPage("DefaultLeftNavbar", new DefaultLeftNavbar());
		mViewPager.setAdapter(pagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// add the menu in the action bar:
		getMenuInflater().inflate(R.menu.example_actionbar_menu, menu);
		return true;
	}

}
