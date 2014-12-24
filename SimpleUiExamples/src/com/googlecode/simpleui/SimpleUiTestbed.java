package com.googlecode.simpleui;

import v2.simpleUi.M_Button;
import v2.simpleUi.M_ButtonBorderless;
import v2.simpleUi.M_Dashboard;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class SimpleUiTestbed extends M_Dashboard {
	public SimpleUiTestbed() {
		super();
		M_ButtonBorderless left = new M_ButtonBorderless(R.drawable.good, "f") {

			@Override
			public void onClick(Context context, View clickedButton) {
				// TODO Auto-generated method stub

			}
		};
		M_ButtonBorderless right = new M_ButtonBorderless(R.drawable.bad) {

			@Override
			public void onClick(Context context, View clickedButton) {
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
