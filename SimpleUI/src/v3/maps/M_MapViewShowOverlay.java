package v3.maps;

import v2.simpleUi.util.SimpleAsyncTask;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;

public abstract class M_MapViewShowOverlay extends M_GoogleMapsV2 {

	@Override
	public void onMapViewIsReadyForTheFirstTime(
			final GoogleMapsV2View googleMapsV2View, FragmentActivity activity,
			GoogleMap map) {
		GoogleMapsV2View.initDefaultBehavior(googleMapsV2View);
		addOverlaysToDisplay(activity, this.getMapView());
		new SimpleAsyncTask() {

			@Override
			public void onRun() {
				Location lastKnownUserPosition = new GeoUtils(
						googleMapsV2View.getActivity()).getLastKnownPosition();
				if (lastKnownUserPosition != null) {
					onFirstTimeUserPositionKnown(googleMapsV2View,
							lastKnownUserPosition);
				}
			}
		}.run();
	}

	public abstract void addOverlaysToDisplay(FragmentActivity context,
			I_MapView mapView);

	/**
	 * override this if the map should not be centered on the user position and
	 * on something else instead
	 * 
	 * @param googleMapsV2View
	 * @param lastKnownUserPosition
	 */
	public void onFirstTimeUserPositionKnown(
			final GoogleMapsV2View googleMapsV2View,
			Location lastKnownUserPosition) {
		googleMapsV2View.setMapCenterTo(lastKnownUserPosition, 17);
	}

}
