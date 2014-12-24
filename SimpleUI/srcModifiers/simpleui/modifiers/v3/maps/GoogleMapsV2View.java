package simpleui.modifiers.v3.maps;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;

import simpleui.modifiers.v3.maps.SimpleGestureListener.SimpleTouchEventInterface;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("ValidFragment")
public class GoogleMapsV2View extends SupportMapFragment implements I_MapView,
		SimpleTouchEventInterface, OnMarkerClickListener, OnMarkerDragListener,
		OnCameraChangeListener {

	public interface MapsV2EventListener extends OnMyLocationChangeListener {
		/**
		 * now methods like
		 * {@link GoogleMapsV2View#initDefaultBehavior(GoogleMapsV2View)} can be
		 * called
		 * 
		 * @param googleMapsV2View
		 * @param activity
		 * @param mapController
		 */
		void onMapViewIsReadyForTheFirstTime(GoogleMapsV2View googleMapsV2View,
				FragmentActivity activity, GoogleMap mapController);

		void onNewAreaOnMapIsShown(LatLng topLeft, LatLng bottomRight,
				int zoomLevel);

		void onLongPress(FragmentActivity activity, GoogleMap map, LatLng gpsPos);

		void onSingleTab(FragmentActivity activity, GoogleMap map, LatLng gpsPos);

		void onDoubleTab(FragmentActivity activity, GoogleMap map, LatLng gpsPos);
	}

	public interface MarkerListener {

		int DRAG_START = 1;
		int DRAG = 2;
		int DRAG_END = 3;

		/**
		 * @param marker
		 * @return true if the marker consumed the click event
		 */
		boolean onTab(Marker marker);

		/**
		 * @param marker
		 * @param eventType
		 *            can be {@link MarkerListener#DRAG_START},
		 *            {@link MarkerListener#DRAG} or
		 *            {@link MarkerListener#DRAG_END}
		 */
		void onDragEvent(Marker marker, int eventType);

	}

	public static class Overlay extends LinkedHashSet<Marker> {

		private Integer defaultIconId;
		private final GoogleMapsV2View googleMap;

		public Overlay(GoogleMapsV2View googleMap) {
			this.googleMap = googleMap;
		}

		public Overlay(GoogleMapsV2View googleMap, Integer defaultIconId) {
			this(googleMap);
			this.defaultIconId = defaultIconId;
		}

		@Override
		public void clear() {
			for (Marker m : this) {
				final Marker marker = m;
				if (Looper.getMainLooper().getThread() == Thread
						.currentThread()) {
					// On UI thread.
					Log.d(LOG_TAG, "overlay.clear from ui thread");
					removeMarkerFromMapInUiThread(marker);
				} else {
					// Not on UI thread.
					Log.d(LOG_TAG, "overlay.clear NOT from ui thread");
					myHandler.post(new Runnable() {

						@Override
						public void run() {
							removeMarkerFromMapInUiThread(marker);
						}
					});
				}
			}
			super.clear();
		}

		private void removeMarkerFromMapInUiThread(Marker marker) {
			if (googleMap.listeners.remove(marker) == null) {
				Log.w(LOG_TAG, "listener could not be removed for marker "
						+ marker);
			}
			marker.remove(); // marker removes itself from the map
		}

		// @Deprecated
		// public boolean add(MarkerOptions markerOptions) {
		// OverlayOptions.setIconIfNeeded(markerOptions, defaultIconId);
		// if (googleMap == null) {
		// Log.w(LOG_TAG, "googleMap was null, cant add new marker");
		// return false;
		// }
		// return add(googleMap.getMap().addMarker(markerOptions));
		// }

		@Override
		public boolean remove(Object object) {
			if (contains(object)) {
				Log.d(LOG_TAG, "overlay.remove");
				((Marker) object).remove();
				if (googleMap.listeners.remove(object) == null) {
					Log.w(LOG_TAG, "listener could not be removed for marker "
							+ object);
				}
				return super.remove(object);
			}
			return false;
		}

		public Marker add(LatLng pos, boolean moovable,
				MarkerListener newWaveMarkerListener) {
			return add(pos, defaultIconId, moovable, newWaveMarkerListener);
		}

		public Marker add(LatLng pos, Bitmap markerIcon, boolean moovable,
				MarkerListener markerListener) {
			BitmapDescriptor icon = BitmapDescriptorFactory
					.fromBitmap(markerIcon);
			return add(pos, moovable, markerListener, icon);
		}

		public Marker add(LatLng pos, int markerDrawableId, boolean moovable,
				MarkerListener markerListener) {
			BitmapDescriptor icon = BitmapDescriptorFactory
					.fromResource(markerDrawableId);
			return add(pos, moovable, markerListener, icon);
		}

		private Marker add(LatLng pos, boolean moovable,
				MarkerListener markerListener, BitmapDescriptor icon) {
			if (googleMap.listeners.containsValue(markerListener)) {
				Log.e(LOG_TAG,
						"Element with same listener was already in the set, will not be added");
				return null;
			}
			MarkerOptions options = new MarkerOptions().position(pos)
					.icon(icon).draggable(moovable);
			if (googleMap.getMap() != null) {
				Marker marker = googleMap.getMap().addMarker(options);
				googleMap.listeners.put(marker, markerListener);
				add(marker);
				Log.d(LOG_TAG, "overlay.add marker");
				return marker;
			}
			Log.w(LOG_TAG, "googleMap.getMap() was null");
			return null;
		}

	}

	private static final String LOG_TAG = "GoogleMapsV2View";

	private FrameLayout container;
	private ArrayList<Overlay> overlays;
	private final boolean longPressEnanabled = true;
	private GestureDetector myGestureDetector;
	/**
	 * If the screen is rotated e.g. the
	 * {@link GoogleMapsV2View#onViewCreated(View, Bundle)} method is called
	 * again. this flag can be used to reinitialize the map
	 */
	private boolean firstTimeThisMapIsShown = true;
	private final HashMap<Marker, MarkerListener> listeners = new HashMap<Marker, GoogleMapsV2View.MarkerListener>();

	private MapsV2EventListener eventListener;

	private static Handler myHandler = new Handler(Looper.getMainLooper());

	public GoogleMapsV2View(FragmentActivity a, MapsV2EventListener l) {
		this.eventListener = l;
		container = new FrameLayout(a) {
			@Override
			public boolean dispatchTouchEvent(MotionEvent event) {
				/*
				 * block scolling of parents when the view is touched
				 */
				getParent().requestDisallowInterceptTouchEvent(true);

				if (event.getAction() == MotionEvent.ACTION_UP) {
					onNewAreaOnMapIsShown(true);
				}

				/*
				 * to understand the idea of redirecting the events look at the
				 * MyGestureListener class which will inform the map when a
				 * special event like a tab, doubleTab, LongPress, .. happened
				 */
				if (!super.dispatchTouchEvent(event)) {
					/*
					 * first the map should be asked to handle the scroll event
					 * on its own, if it is not clickable this part here is
					 * called and false can be returned directly, if it is
					 * clickable the event will be passed on to the
					 * myGestureDetector
					 * 
					 * the map is disabled in this case so return false:
					 */
					return false;
				}
				// else pass the event to the gesture detector
				return myGestureDetector.onTouchEvent(event);

			}

		};

		int MAPS_CONTAINER_ID = (int) (new Date().getTime() + 10000 * Math
				.random());
		container.setId(MAPS_CONTAINER_ID);
		container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		FragmentTransaction ft = a.getSupportFragmentManager()
				.beginTransaction();
		ft.add(MAPS_CONTAINER_ID, this).commit();

		// create a GestureDetector:
		myGestureDetector = new GestureDetector(a, new SimpleGestureListener(
				this));
		myGestureDetector.setIsLongpressEnabled(longPressEnanabled);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// to restore the fragment (e.g. when the screen is rotated):
		setRetainInstance(true);
	}

	public FrameLayout getContainerView() {
		return container;
	}

	@Override
	public LatLng getLatLngForPixelPos(int xPixelPosInMapView,
			int yPixelPosInMapView) {
		return getMap().getProjection().fromScreenLocation(
				new Point(xPixelPosInMapView, yPixelPosInMapView));
	}

	public void setMapCenterTo(Location location, int zoomLevel) {
		if (location != null) {
			setMapCenterTo(
					new LatLng(location.getLatitude(), location.getLongitude()),
					zoomLevel);
		} else {
			Log.e(LOG_TAG, "Passed location to set center to was null");
		}
	}

	public void setMapCenterTo(Location location) {
		setMapCenterTo(
				new LatLng(location.getLatitude(), location.getLongitude()),
				null);
	}

	@Override
	public boolean setMapCenterTo(final LatLng pos, final Integer zoomLevel) {
		if (pos != null) {
			if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
				// On UI thread.
				centerAndZoomOnUiThread(pos, zoomLevel);
			} else {
				// Not on UI thread.
				myHandler.post(new Runnable() {

					@Override
					public void run() {
						centerAndZoomOnUiThread(pos, zoomLevel);
					}
				});
			}
			return true;
		}
		return false;
	}

	private void centerAndZoomOnUiThread(final LatLng pos,
			final Integer zoomLevel) {
		if (zoomLevel == null) {
			getMap().animateCamera(CameraUpdateFactory.newLatLng(pos));
		} else {
			getMap().animateCamera(
					CameraUpdateFactory.newLatLngZoom(pos, zoomLevel));
		}
	}

	@Override
	public boolean clearAllOverlays() {
		try {
			for (Overlay o : getOverlays()) {
				o.clear();
			}
			this.getOverlays().clear();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private ArrayList<Overlay> getOverlays() {
		if (overlays == null) {
			overlays = new ArrayList<GoogleMapsV2View.Overlay>();
		}
		return overlays;
	}

	// @Deprecated
	// private Overlay generateOverlayFrom(OverlayOptions mapOverlay) {
	// Overlay result = new Overlay(this);
	// for (MarkerOptions markerOptions : mapOverlay) {
	// result.add(getMap().addMarker(markerOptions));
	// }
	// return result;
	// }

	@Override
	public boolean removeOverlay(Overlay overlay) {
		try {
			overlay.clear();
			return this.getOverlays().remove(overlay);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Overlay addNewEmptyOverlay(int defaultIconId) {
		if (getMap() == null) {
			Log.e(LOG_TAG, "map was null, mapview not yet initialized");
			return null;
		}
		Overlay newOverlay = new Overlay(this, defaultIconId);
		getOverlays().add(newOverlay);
		return newOverlay;
	}

	// @Override
	// public Overlay generateOverlay(OverlayOptions overlayOptions) {
	// Overlay overlay = generateOverlayFrom(overlayOptions);
	// getOverlays().add(overlay);
	// return overlay;
	// }

	@Override
	public int getZoomLevel() {
		return (int) getMap().getCameraPosition().zoom;
	}

	@Override
	public boolean setZoomLevel(int level) {
		getMap().animateCamera(CameraUpdateFactory.zoomTo(level));
		return true;
	}

	@Override
	public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
	}

	@Override
	public void onLongPress(MotionEvent e) {

		LatLng gpsPos = getLatLngPosFromClickEvent(e);
		eventListener.onLongPress(getActivity(), getMap(), gpsPos);
	}

	@Override
	public void onSingleTab(MotionEvent e) {
		if (!markerConsumedClick) {
			LatLng gpsPos = getLatLngPosFromClickEvent(e);
			eventListener.onSingleTab(getActivity(), getMap(), gpsPos);
		}
		markerConsumedClick = false;
	}

	public void enableZoomButtons(boolean showThem) {
		getMap().getUiSettings().setZoomControlsEnabled(showThem);
	}

	@Override
	public void onDoubleTap(MotionEvent e) {
		LatLng gpsPos = getLatLngPosFromClickEvent(e);
		eventListener.onDoubleTab(getActivity(), getMap(), gpsPos);
	}

	public LatLng getLatLngPosFromClickEvent(MotionEvent e) {
		return getLatLngForPixelPos((int) e.getX(), (int) e.getY());
	}

	private Long lastTime = null;
	private boolean markerConsumedClick = false;

	/**
	 * will send onNewAreaOnMapIsShown in an max interval of 1 second
	 * 
	 * @param forceEvent
	 *            set to true to trigger the event even if the last event was
	 *            shortly before
	 */
	private void onNewAreaOnMapIsShown(boolean forceEvent) {

		long currentTime = System.currentTimeMillis();
		if (lastTime != null && currentTime - lastTime < 1000) {
			return;
		}
		lastTime = currentTime;

		LatLng topLeft = getMap().getProjection().fromScreenLocation(
				new Point(0, 0));
		LatLng bottomRight = getMap().getProjection().fromScreenLocation(
				new Point(getContainerView().getWidth(), getContainerView()
						.getHeight()));
		eventListener.onNewAreaOnMapIsShown(topLeft, bottomRight,
				getZoomLevel());
	}

	public void setSatellite(boolean b) {
		GoogleMap m = getMap();
		if (m == null) {
			Log.w(LOG_TAG, "getMap() was null when setSatellite() called");
			return;
		}
		if (b) {
			m.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		} else {
			m.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}

	}

	private void showUserLocation(boolean userPositionDisplayed) {
		if (getMap() != null) {
			getMap().setMyLocationEnabled(userPositionDisplayed);
		} else {
			Log.w(LOG_TAG, "getMap() was null when showUserLocation() called");
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// now the map is ready
		if (getMap() != null && firstTimeThisMapIsShown) {
			Log.d(LOG_TAG, "First time the map is shown");
			getMap().setOnMyLocationChangeListener(eventListener);
			getMap().setOnMarkerClickListener(this);
			getMap().setOnCameraChangeListener(this);
			getMap().setOnMarkerDragListener(this);
			eventListener.onMapViewIsReadyForTheFirstTime(this, getActivity(),
					getMap());
			firstTimeThisMapIsShown = false;
		} else if (getMap() == null) {
			Log.w(LOG_TAG, "getMap() was null when onViewCreated was called!");
			Toast.makeText(getActivity(), "Update your Google Maps",
					Toast.LENGTH_LONG).show();
		} else {
			Log.d(LOG_TAG,
					"Map view was restored, not calling onMapViewIsReady(..)");
			Log.d(LOG_TAG, "    > getMap() " + getMap());
		}
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		onNewAreaOnMapIsShown(false);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		MarkerListener listener = listeners.get(marker);
		if (listener != null) {
			if (listener.onTab(marker)) {
				markerConsumedClick = true;
				return true;
			}
		} else {
			Log.w(LOG_TAG,
					"No listener found for clicked marker. listeners.size()="
							+ listeners.size());
		}
		return false;
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		sendDragEventToMarkerListener(marker, MarkerListener.DRAG_START);
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		sendDragEventToMarkerListener(marker, MarkerListener.DRAG);
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		sendDragEventToMarkerListener(marker, MarkerListener.DRAG_END);
	}

	private void sendDragEventToMarkerListener(Marker marker, final int type) {
		MarkerListener listener = listeners.get(marker);
		if (listener != null) {
			listener.onDragEvent(marker, type);
		}
	}

	public static boolean initDefaultBehavior(GoogleMapsV2View googleMapsV2View) {
		if (googleMapsV2View.getMap() == null) {
			Log.e(LOG_TAG, "Cant be called before the map is initialized!");
			return false;
		}
		Log.d(LOG_TAG, "Init default map settings");
		googleMapsV2View.setSatellite(false);
		googleMapsV2View.showUserLocation(true);
		UiSettings mapUiSettings = googleMapsV2View.getMap().getUiSettings();
		mapUiSettings.setZoomControlsEnabled(false);
		// mapUiSettings.setMyLocationButtonEnabled(false);
		return true;
	}

}
