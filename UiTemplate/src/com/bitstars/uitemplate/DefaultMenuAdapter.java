package com.bitstars.uitemplate;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import fragments.MenuBaseAdapter;

public class DefaultMenuAdapter extends MenuBaseAdapter {

	public DefaultMenuAdapter(Context context, int menuRes) {
		super(context, menuRes);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_navigation, null);
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

}
