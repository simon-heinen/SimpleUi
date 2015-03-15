package simpleui.examples.maps;

import simpleui.examples.R;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_SeperatorLine;
import simpleui.modifiers.v3.maps.M_GoogleMapsMarkLocation;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class M_GoogleMapsMarkLocationTests extends M_Container {

	protected static final String LOG_TAG = "GoogleMapV2PosOnMapTests";

	public M_GoogleMapsMarkLocationTests() {
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
