package fragments;

import java.lang.reflect.Constructor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitstars.uitemplate.R;

public class MenuBaseAdapter extends BaseAdapter {

	private final Context mContext;
	private final LayoutInflater mInflater;
	private final MenuInflater mMenuInflater;
	private final int mMenuRes;
	private final Menu mMenu;

	public MenuBaseAdapter(Context context, int menuRes) {
		super();
		mContext = context;
		mInflater = LayoutInflater.from(context);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_navigation, null);

			holder = new ViewHolder();
			holder.setIcon((ImageView) convertView.findViewById(R.id.icon));
			holder.setTitle((TextView) convertView.findViewById(R.id.title));
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MenuItem item = getItem(position);
		holder.getIcon().setImageDrawable(item.getIcon());
		holder.getTitle().setText(item.getTitle());

		return convertView;
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

	private class ViewHolder {
		private TextView mTitle;
		private ImageView mIcon;

		private ImageView getIcon() {
			return mIcon;
		}

		private void setIcon(ImageView mIcon) {
			this.mIcon = mIcon;
		}

		private TextView getTitle() {
			return mTitle;
		}

		private void setTitle(TextView mTitle) {
			this.mTitle = mTitle;
		}
	}
}
