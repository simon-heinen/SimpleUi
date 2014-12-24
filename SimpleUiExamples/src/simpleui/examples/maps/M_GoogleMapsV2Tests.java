package simpleui.examples.maps;

import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_SeperatorLine;
import simpleui.modifiers.v3.maps.GoogleMapsV2View;
import simpleui.modifiers.v3.maps.M_GoogleMapsV2;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class M_GoogleMapsV2Tests extends M_Container {

	public M_GoogleMapsV2Tests() {
		add(M_SeperatorLine.newDefaultOne());
		add(new M_GoogleMapsV2() {

			@Override
			public int getMapHeigthInDip() {
				return 400;
			}

			@Override
			public void onMapViewIsReadyForTheFirstTime(
					GoogleMapsV2View googleMapsV2View,
					FragmentActivity activity, GoogleMap map) {
				GoogleMapsV2View.initDefaultBehavior(googleMapsV2View);
			}

			@Override
			public void onNewAreaOnMapIsShown(LatLng topLeft,
					LatLng bottomRight, int zoomLevel) {
				// TODO Auto-generated method stub
				super.onNewAreaOnMapIsShown(topLeft, bottomRight, zoomLevel);
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
