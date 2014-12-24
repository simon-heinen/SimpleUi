package simpleui.modifiers.v3.maps;

import simpleui.modifiers.v3.maps.GoogleMapsV2View.Overlay;

import com.google.android.gms.maps.model.LatLng;

public interface I_MapView {

	LatLng getLatLngForPixelPos(int xPixelPosInMapView, int yPixelPosInMapView);

	/**
	 * @param pos
	 * @param zoomLevel
	 *            pass null to leave the zoom level untouched
	 * @return
	 */
	boolean setMapCenterTo(LatLng pos, Integer zoomLevel);

	boolean clearAllOverlays();

	Overlay addNewEmptyOverlay(int defaultIconId);

	int getZoomLevel();

	boolean setZoomLevel(int level);

	boolean removeOverlay(Overlay overlay);

}
