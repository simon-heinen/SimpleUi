package simpleui.modifiers.v3.maps;

import simpleui.modifiers.v3.maps.GoogleMapsV2View.MarkerListener;
import simpleui.modifiers.v3.maps.GoogleMapsV2View.Overlay;
import android.app.Activity;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public abstract class M_GoogleMapsMarkLocation extends M_GoogleMapsV2 {

	private static final String LOG_TAG = "M_GoogleMapsMarkLocation";
	private int markerIconId;
	private LatLng markedPosition;
	private Overlay overlay;

	public M_GoogleMapsMarkLocation(int markerIconId) {
		this.markerIconId = markerIconId;

	}

	private Overlay getOverlay() {
		if (overlay == null) {
			overlay = new Overlay(getMapView(), markerIconId);
		}
		return overlay;
	}

	@Override
	public void onDevicePosUpdate(Activity activity, I_MapView mapView,
			LatLng pos, Location posAsLocation, boolean firstUpdate) {
		if (firstUpdate) {
			getMapView().setMapCenterTo(pos, 17);
		}
	}

	@Override
	public void onMapViewIsReadyForTheFirstTime(
			GoogleMapsV2View googleMapsV2View, FragmentActivity activity,
			GoogleMap mapController) {
		GoogleMapsV2View.initDefaultBehavior(googleMapsV2View);
	}

	@Override
	public void onSingleTab(FragmentActivity activity, GoogleMap map,
			LatLng gpsPos) {
		Log.d(LOG_TAG, "onSingleTab");
		setMarkedPosition(gpsPos);
	}

	public void setMarkedPosition(final LatLng markedPosition) {
		this.markedPosition = markedPosition;
		getOverlay().clear();
		getOverlay().add(markedPosition, true, new MarkerListener() {

			@Override
			public boolean onTab(Marker marker) {
				Log.d(LOG_TAG, "marker onTab");
				// clear marker again
				getOverlay().clear();
				marker.remove();
				M_GoogleMapsMarkLocation.this.markedPosition = null;
				return true;
			}

			@Override
			public void onDragEvent(Marker marker, int type) {
				if (type == DRAG_END) {
					// update position
					M_GoogleMapsMarkLocation.this.markedPosition = marker
							.getPosition();
				}
			}
		});
	}

	@Override
	public boolean save() {
		if (markedPosition != null) {
			return save(markedPosition);
		} else {
			return onNoPositionOnMapMarked();
		}
	}

	/**
	 * @return true of its ok that the user did not mark any location on the
	 *         map, false if he has to choose one
	 */
	public abstract boolean onNoPositionOnMapMarked();

	public abstract boolean save(LatLng geoPoint);

}
