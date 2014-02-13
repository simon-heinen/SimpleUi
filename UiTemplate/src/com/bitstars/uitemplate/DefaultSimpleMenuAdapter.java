package com.bitstars.uitemplate;

import com.bitstars.uitemplate.R;
import com.bitstars.uitemplate.R.id;
import com.bitstars.uitemplate.R.layout;

import adapters.SimpleMenuAdapter;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DefaultSimpleMenuAdapter extends SimpleMenuAdapter {

	public DefaultSimpleMenuAdapter(Context context, int menuRes) {
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
			convertView = inflater.inflate(R.layout.default_navbar_item, null);
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
