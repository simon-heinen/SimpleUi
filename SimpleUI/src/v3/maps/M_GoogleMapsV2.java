package v3.maps;

import v2.simpleUi.ModifierInterface;
import v3.maps.GoogleMapsV2View.MapsV2EventListener;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * - Has to be inside a {@link FragmentActivity}
 * 
 * - Only one {@link M_GoogleMapsV2} per {@link FragmentActivity} possible
 * 
 * @author Spobo
 * 
 */
public abstract class M_GoogleMapsV2 implements ModifierInterface,
		MapsV2EventListener {

	private static final String LOG_TAG = "M_GoogleMapsV2";
	private GoogleMapsV2View mapView;
	private boolean isFirstUpdate = true;

	@Override
	public View getView(Context context) {
		mapView = new GoogleMapsV2View((FragmentActivity) context, this);
		FrameLayout v = mapView.getContainerView();
		// the map is in a scroll view so the default match parrend wount help
		// for the height:
		v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				getMapHeight()));
		return v;
	}

	@Override
	public void onMyLocationChange(Location location) {
		onDevicePosUpdate(mapView.getActivity(), mapView, toLatLng(location),
				isFirstUpdate);
		isFirstUpdate = false;
	}

	public LatLng toLatLng(Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}

	public void onDevicePosUpdate(Activity activity, I_MapView mapView,
			LatLng pos, boolean firstUpdate) {
	}

	public GoogleMapsV2View getMapView() {
		return mapView;
	}

	public abstract int getMapHeight();

	/**
	 * @param topLeft
	 * @param bottomRight
	 * @param zoomLevel
	 *            between 0 (whole world) and 21 (closest)
	 */
	@Override
	public void onNewAreaOnMapIsShown(LatLng topLeft, LatLng bottomRight,
			int zoomLevel) {
		Log.d(LOG_TAG, "onNewAreaOnMapIsShown from " + topLeft + " to "
				+ bottomRight);
	}

	@Override
	public void onLongPress(FragmentActivity activity, GoogleMap map,
			LatLng gpsPos) {
		Log.d(LOG_TAG, "onLongPress at " + gpsPos);
	}

	@Override
	public void onSingleTab(FragmentActivity activity, GoogleMap map,
			LatLng gpsPos) {
		Log.d(LOG_TAG, "onSingleTab at " + gpsPos);
	}

	@Override
	public void onDoubleTab(FragmentActivity activity, GoogleMap map,
			LatLng gpsPos) {
		Log.d(LOG_TAG, "onDoubleTap at pos " + gpsPos);
	}

	@Override
	public boolean save() {
		return true;
	}

}
