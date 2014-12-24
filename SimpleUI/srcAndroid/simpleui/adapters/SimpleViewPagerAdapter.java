package simpleui.adapters;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleViewPagerAdapter extends FragmentPagerAdapter {

	private final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private final ArrayList<String> fragmentNames = new ArrayList<String>();

	public SimpleViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return fragmentNames.get(position);
	}

	public void addPage(String fragmentName, Fragment page) {
		fragmentNames.add(fragmentName);
		fragments.add(page);
	}
}
