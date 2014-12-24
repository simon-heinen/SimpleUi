package simpleui.adapters;

import java.lang.reflect.Constructor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.BaseAdapter;

public abstract class SimpleMenuAdapter extends BaseAdapter {

	protected final LayoutInflater inflater;
	private final MenuInflater mMenuInflater;
	private final int mMenuRes;
	private final Menu mMenu;

	public SimpleMenuAdapter(Context context, int menuRes) {
		super();
		inflater = LayoutInflater.from(context);
		mMenuInflater = new MenuInflater(context);
		mMenuRes = menuRes;
		mMenu = newMenuInstance(context);
		mMenuInflater.inflate(mMenuRes, mMenu);
		notifyDataSetChanged();
	}

	/**
	 * http://stackoverflow.com/questions/14118820/how-to-create-a-menu-instance
	 * -programmatically-i-e-inflate-a-menu-outside-oncr
	 * 
	 * @param context
	 * @return
	 */
	private Menu newMenuInstance(Context context) {
		try {
			Class<?> menuBuilderClass = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Constructor<?> constructor = menuBuilderClass
					.getDeclaredConstructor(Context.class);
			return (Menu) constructor.newInstance(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public MenuItem getItem(int position) {
		return mMenu.getItem(position);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public int getCount() {
		return mMenu.size();
	}

}
