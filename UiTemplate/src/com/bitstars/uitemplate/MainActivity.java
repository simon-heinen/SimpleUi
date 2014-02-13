package com.bitstars.uitemplate;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import fragments.DraggableListViewTestFragment;
import fragments.MainFragment;
import fragments.SimpleViewPagerAdapter;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
		SimpleViewPagerAdapter pagerAdapter = new SimpleViewPagerAdapter(
				getSupportFragmentManager());
		pagerAdapter.addPage("Test", new MainFragment());
		pagerAdapter.addPage("Test2", new DraggableListViewTestFragment());
		mViewPager.setAdapter(pagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// add the menu in the action bar:
		getMenuInflater().inflate(R.menu.example_actionbar_menu, menu);
		return true;
	}

}
