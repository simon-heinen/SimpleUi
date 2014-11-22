package com.googlecode.simpleui;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_Dashboard;
import v2.simpleUi.M_IconButtonWithText;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

public class SimpleUiTestbed extends M_Dashboard {
	public SimpleUiTestbed() {
		super();
		M_IconButtonWithText left = new M_IconButtonWithText(R.drawable.good, "f") {

			@Override
			public void onClick(Context context, ImageView clickedButton) {
				// TODO Auto-generated method stub

			}
		};
		M_IconButtonWithText right = new M_IconButtonWithText(R.drawable.bad) {

			@Override
			public void onClick(Context context, ImageView clickedButton) {
				// TODO Auto-generated method stub

			}
		};
		add(left);
		add(right);

		add(new M_Button("f") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				// TODO Auto-generated method stub

			}
		});
		add(right);
		add(left);
		add(new M_Button("swefa") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				// TODO Auto-generated method stub

			}
		});
		add(new M_Button("swefa") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				// TODO Auto-generated method stub

			}
		});
		add(new M_Button("swefa") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				// TODO Auto-generated method stub

			}
		});

	}
}
