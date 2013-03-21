package v3.maps;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public abstract class M_GoogleMapsMarkLocation extends M_GoogleMapsV2 {

	private int markerIconId;

	public M_GoogleMapsMarkLocation(int markerIconId) {
		this.markerIconId = markerIconId;
	}

	@Override
	public void onMapViewIsReadyForTheFirstTime(
			GoogleMapsV2View googleMapsV2View, FragmentActivity activity,
			GoogleMap mapController) {
		// TODO Auto-generated method stub

	}

	public abstract void onNoPositionOnMapMarked();

	public abstract boolean save(LatLng geoPoint);

}
