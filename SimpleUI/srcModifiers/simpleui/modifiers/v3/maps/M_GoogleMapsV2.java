package simpleui.modifiers.v3.maps;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v3.maps.GoogleMapsV2View.MapsV2EventListener;
import simpleui.util.GeoUtils;
import simpleui.util.ImageTransform;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
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
	private Resources resources;
	private boolean isFirstUpdate = true;
	private static Location currentUserPos;

	@Override
	public View getView(Context context) {
		mapView = new GoogleMapsV2View((FragmentActivity) context, this);
		FrameLayout v = mapView.getContainerView();
		resources = v.getResources();
		// the map is in a scroll view so the default match parrend wount help
		// for the height:
		v.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				getMapHeight()));
		return v;
	}

	@Override
	public void onMyLocationChange(Location location) {
		currentUserPos = location;
		onDevicePosUpdate(mapView.getActivity(), mapView, toLatLng(location),
				location, isFirstUpdate);
		isFirstUpdate = false;
	}

	public LatLng getCurrentUserLocationAsLatLng(Context context) {
		return toLatLng(getCurrentUserLocation(context));
	}

	/**
	 * iterates through the different map types like satellite, normal map, etc
	 */
	public void switchToNextMapType() {
		if (mapView != null && mapView.getMap() != null) {
			int type = mapView.getMap().getMapType();
			if (type == GoogleMap.MAP_TYPE_NORMAL) {
				type = GoogleMap.MAP_TYPE_SATELLITE;
			} else if (type == GoogleMap.MAP_TYPE_SATELLITE) {
				type = GoogleMap.MAP_TYPE_HYBRID;
			} else if (type == GoogleMap.MAP_TYPE_HYBRID) {
				type = GoogleMap.MAP_TYPE_TERRAIN;
			} else if (type == GoogleMap.MAP_TYPE_TERRAIN) {
				type = GoogleMap.MAP_TYPE_NORMAL;
			}
			mapView.getMap().setMapType(type);
		}
	}

	public Location getCurrentUserLocation(Context context) {
		if (currentUserPos == null) {
			currentUserPos = mapView.getMap().getMyLocation();
			if (currentUserPos == null) {
				Log.i(LOG_TAG, "Current position not known jet, "
						+ "will use last known position instead");
				currentUserPos = new GeoUtils(context).getLastKnownPosition();
			}
		}
		return currentUserPos;
	}

	public LatLng toLatLng(Location location) {
		if (location == null) {
			Log.e(LOG_TAG, "location=null was passed to toLatLng");
			return null;
		}
		return new LatLng(location.getLatitude(), location.getLongitude());
	}

	/**
	 * @param activity
	 * @param mapView
	 * @param currentUserPos
	 * @param currentUserPosAsLocation
	 * @param firstUpdate
	 *            true if this method is called the first time
	 */
	public void onDevicePosUpdate(Activity activity, I_MapView mapView,
			LatLng currentUserPos, Location currentUserPosAsLocation,
			boolean firstUpdate) {
	}

	public int getDisplayHeightInDip(Activity a) {
		Display display = a.getWindowManager().getDefaultDisplay();
		return (int) ImageTransform.PixelsToDip(a, display.getHeight());
	}

	public GoogleMapsV2View getMapView() {
		return mapView;
	}

	/**
	 * overwrite this method to return values like
	 * {@link LayoutParams#MATCH_PARENT}
	 * 
	 * @return
	 */
	public int getMapHeight() {
		return (int) ImageTransform.dipToPixels(resources, getMapHeigthInDip());
	}

	/**
	 * called by {@link M_GoogleMapsV2#getMapHeight()} on default to allow the
	 * same map size on every device
	 * 
	 * @return
	 */
	public abstract int getMapHeigthInDip();

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
