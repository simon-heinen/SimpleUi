package simpleui.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

/**
 * This util class is a collection of all common location related operations
 * like enabling GPS, receiving the current position, mapping an address to gps
 * coordinates or converting to correct position values
 * {@link GeoUtils#convertDegreesMinutesSecondsToDecimalDegrees(double, double, double)}
 * 
 * @author Spobo
 * 
 */
public class GeoUtils {

	private static final String LOG_TAG = "Geo Utils";
	private static final String POSITION_BACKUP_FILE = "simpleui_geoutils_posbackup";
	private final Geocoder myGeoCoder;
	private final Context myContext;

	public GeoUtils(Context context) {
		myContext = context;
		myGeoCoder = new Geocoder(context);
	}

	/**
	 * Use this method if you have to convert to decimal degrees.
	 * 
	 * Example usage: <br>
	 * 16o 19' 28,29" to 16,324525o
	 * 
	 * @param degree
	 *            16
	 * @param minutes
	 *            19
	 * @param seconds
	 *            28,29
	 * @return 16,324525o
	 */
	public static double convertDegreesMinutesSecondsToDecimalDegrees(
			double degree, double minutes, double seconds) {
		return degree + ((minutes + (seconds / 60)) / 60) / 60;
	}

	/**
	 * This method returns the best match for a specified position. It could for
	 * example be used to calculate the closest address to your current
	 * location.
	 * 
	 * @param lati
	 * @param longi
	 * 
	 * @param location
	 * @return the closest address to the
	 */
	public Address getBestAddressForLocation(double lati, double longi) {
		try {
			List<Address> locations = myGeoCoder
					.getFromLocation(lati, longi, 1);
			if (locations.size() > 0) {
				return locations.get(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the position of an specified address (Streetname e.g.)
	 * 
	 * @param address
	 * @return null if the address could not be found
	 */
	public List<Address> getBestLocationForAddress(String address) {
		try {
			return myGeoCoder.getFromLocationName(address, 5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isGPSDisabled(Context context) {
		return !((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE))
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public boolean isGPSDisabled() {
		return isGPSDisabled(myContext);
	}

	/**
	 * @param activity
	 * @return true if GPS could be enabled without user interaction, else the
	 *         settings will be started and false is returned
	 */
	public static boolean enableGPS(Activity activity) {
		return switchGPS(activity, true, true);
	}

	/**
	 * This method will activate gps if it is disabled
	 * 
	 * @param activity
	 */
	public static void enableLocationProvidersIfNeeded(Activity activity) {
		try {
			if (isGPSDisabled(activity)) {
				switchGPS(activity, true, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isWifiDisabled(Activity activity) {
		return !((LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE))
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	/**
	 * @param activity
	 * @return true if GPS could be disabled without user interaction, else the
	 *         settings will be started and false is returned
	 */
	public static boolean disableGPS(Activity activity) {
		return disableGPS(activity, false);
	}

	/**
	 * @param activity
	 * @return true if GPS could be disabled without user interaction, else the
	 *         settings will be started and false is returned
	 */
	public static boolean disableGPS(Activity activity,
			boolean showSettingsIfAutoSwitchImpossible) {
		return switchGPS(activity, false, showSettingsIfAutoSwitchImpossible);
	}

	/**
	 * @param activity
	 * @param enableGPS
	 * @param showSettingsIfAutoSwitchImpossible
	 * @return true if GPS could be switched to the desired value without user
	 *         interaction, else the settings will be started and false is
	 *         returned
	 */
	public static boolean switchGPS(Activity activity, boolean enableGPS,
			boolean showSettingsIfAutoSwitchImpossible) {
		if (canTurnOnGPSAutomatically(activity)) {
			String provider = Settings.Secure.getString(
					activity.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			boolean currentlyEnabled = provider.contains("gps");
			if (!currentlyEnabled && enableGPS) {
				pokeGPSButton(activity);
			} else if (currentlyEnabled && !enableGPS) {
				pokeGPSButton(activity);
			}
			return true;
		} else if (showSettingsIfAutoSwitchImpossible) {
			Log.d(LOG_TAG, "Can't enable GPS automatically, will start "
					+ "settings for manual enabling!");
			openLocationSettingsPage(activity);
		}
		return false;
	}

	public static void openLocationSettingsPage(Activity activity) {
		activity.startActivity(new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	/**
	 * Centers the map to the last known position as reported by the most
	 * accurate location provider. If the last location is unknown, a toast
	 * message is displayed instead.
	 * 
	 * @return null if no last known position was found
	 */
	public Location getLastKnownPosition() {
		Location currentLocation;
		Location bestLocation = null;
		LocationManager locationManager = (LocationManager) myContext
				.getSystemService(Context.LOCATION_SERVICE);
		for (String provider : locationManager.getProviders(true)) {
			currentLocation = locationManager.getLastKnownLocation(provider);
			if (currentLocation != null
					&& (bestLocation == null || currentLocation.getAccuracy() < bestLocation
							.getAccuracy())) {
				bestLocation = currentLocation;
			}
		}
		if (bestLocation == null) {
			// if its still null try to load it from the internal storage
			bestLocation = loadLocationFromBackupFile();
		} else {
			savePosToBackupFile(myContext, bestLocation);
		}
		return bestLocation;
	}

	public static void savePosToBackupFile(Context context, Location pos) {
		try {
			HashMap<String, Float> x = new HashMap<String, Float>();
			x.put("latitude", (float) pos.getLatitude());
			x.put("longitude", (float) pos.getLongitude());
			x.put("accuracy", pos.getAccuracy());
			x.put("time", (float) pos.getTime());
			IO.saveSerializableToPrivateStorage(context, POSITION_BACKUP_FILE,
					x);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Position backup could not be saved:");
			e.printStackTrace();
		}
	}

	private Location loadLocationFromBackupFile() {
		try {
			HashMap<String, Float> x = (HashMap<String, Float>) IO
					.loadSerializableFromPrivateStorage(myContext,
							POSITION_BACKUP_FILE);
			Location pos = new Location("lastStoredPos");
			pos.setLatitude(x.get("latitude"));
			pos.setLongitude(x.get("longitude"));
			pos.setAccuracy(x.get("accuracy"));
			pos.setTime((long) (float) x.get("time"));
			return pos;
		} catch (Exception e) {
			Log.e(LOG_TAG, "Position backup could not be loaded:");
			e.printStackTrace();
		}
		return null;
	}

	private static void pokeGPSButton(Activity activity) {
		final Intent poke = new Intent();
		poke.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		poke.setData(Uri.parse("3"));
		activity.sendBroadcast(poke);
	}

	/**
	 * source from
	 * http://stackoverflow.com/questions/4721449/enable-gps-programatically
	 * -like-tasker
	 */
	private static boolean canTurnOnGPSAutomatically(Context c) {
		PackageInfo pacInfo = null;
		try {
			pacInfo = c.getPackageManager().getPackageInfo(
					"com.android.settings", PackageManager.GET_RECEIVERS);
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "com.android.settings package not found");
			return false; // package not found
		}
		if (pacInfo != null) {
			for (ActivityInfo actInfo : pacInfo.receivers) {
				// test if recevier is exported. if so, we can toggle GPS.
				if (actInfo.name
						.equals("com.android.settings.widget.SettingsAppWidgetProvider")
						&& actInfo.exported) {
					return true;
				}
			}
		}
		return false; // default
	}

}
