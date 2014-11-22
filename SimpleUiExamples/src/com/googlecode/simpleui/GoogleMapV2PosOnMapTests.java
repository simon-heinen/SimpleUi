package com.googlecode.simpleui;

import v2.simpleUi.M_Container;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.M_SeperatorLine;
import v3.maps.M_GoogleMapsMarkLocation;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GoogleMapV2PosOnMapTests extends M_Container {

	protected static final String LOG_TAG = "GoogleMapV2PosOnMapTests";

	public GoogleMapV2PosOnMapTests() {
		add(M_SeperatorLine.newDefaultOne());
		add(new M_GoogleMapsMarkLocation(R.drawable.good) {

			@Override
			public int getMapHeigthInDip() {
				return 200;
			}

			@Override
			public boolean save(LatLng geoPoint) {
				Log.i(LOG_TAG, "Selected pos: " + geoPoint);
				return true;
			}

			@Override
			public boolean onNoPositionOnMapMarked() {
				return false;
			}
		});
		add(M_SeperatorLine.newDefaultOne());
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(M_SeperatorLine.newDefaultOne());
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
		add(M_SeperatorLine.newDefaultOne());
		add(new M_InfoText("Blabla"));
		add(new M_InfoText("Blabla"));
	}

}
