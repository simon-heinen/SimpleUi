package simpleui.modifiers.v3.maps;

import android.app.Activity;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public abstract class M_MapViewShowOverlay extends M_GoogleMapsV2 {

	@Override
	public void onMapViewIsReadyForTheFirstTime(
			final GoogleMapsV2View googleMapsV2View,
			final FragmentActivity activity, GoogleMap map) {
		GoogleMapsV2View.initDefaultBehavior(googleMapsV2View);
		addOverlaysToDisplay(activity, this.getMapView());
	}

	@Override
	public void onDevicePosUpdate(Activity activity, I_MapView mapView,
			LatLng pos, Location posAsLocation, boolean firstUpdate) {
		super.onDevicePosUpdate(activity, mapView, pos, posAsLocation,
				firstUpdate);
		if (firstUpdate) {
			onFirstTimeUserPositionKnown((GoogleMapsV2View) mapView,
					posAsLocation);
		}
	}

	public abstract void addOverlaysToDisplay(FragmentActivity context,
			I_MapView mapView);

	/**
	 * override this if the map should not be centered on the user position and
	 * on something else instead
	 * 
	 * @param googleMapsV2View
	 * @param currentUserPosition
	 */
	public void onFirstTimeUserPositionKnown(
			final GoogleMapsV2View googleMapsV2View,
			Location currentUserPosition) {
		googleMapsV2View.setMapCenterTo(currentUserPosition, 17);
	}

}