package simpleui.geo;

import simpleui.util.Log;

/**
 * Utility class to perform a remapping of GPS coordinates to the internal
 * coordinate system.
 */
public class CoordRemap {

	private static final String LOG_TAG = "CoordRemap";

	/**
	 * Conversion of angles in degress to radians.
	 */
	protected static final float DEG2RAD = (float) (Math.PI) / 180f;

	// Constants:

	/**
	 * earth circumfence at equator is 40075017
	 */
	protected static final float equator = 40075017f;
	/**
	 * earth circumfence through poles is 40008000
	 */
	protected static final float poles = 40008000f;

	/**
	 * Meters per degree.
	 */
	protected static final float lng2rad = equator / 360f;
	protected static final float lat2rad = poles / 360f;

	/**
	 * Remaps GPS coordinates to a (x,y,z) coordinate system.<br>
	 * 
	 * @param result
	 *            - Array of length 3 the coordinates are written to.<br>
	 *            <code>result[0]</code> = north in meters (X AXIS).<br>
	 *            <code>result[2]</code> = east in meters (Z AXIS).
	 * @param latitude
	 * @param longitude
	 * @param zeroLat
	 * @param zeroLng
	 */
	public static void calcVirtualPos(float[] result, double latitude,
			double longitude, double zeroLat, double zeroLng) {
		if (result.length != 3) {
			Log.e(LOG_TAG,
					"Length of passed result array did not have the correct length 3");
			Thread.dumpStack();
		}

		/*
		 * Assuming the earth is nearly a sphere. Longitude (german:
		 * Laengengrad) points from North to South. Earth circumference in this
		 * direction would be around 4.000.800 meters.
		 * 
		 * The longitude calculation depends on current latitude: The
		 * circumference of a circle at a given latitude is proportional to the
		 * cosine, so the formula is:
		 * 
		 * (myLongitude - zeroLongitude) * 40075017 / 360 * cos(zeroLatitude)
		 * 
		 * 
		 * earth circumfence at equator is 40.075.017 meters (WGS 84)
		 * 
		 * degree to radians: PI/180=0.0174532925
		 */

		// This is a snippet implementing the above:
		result[0] = (float) ((latitude - zeroLat) * lat2rad);
		result[2] = (float) ((longitude - zeroLng) * lng2rad * Math.cos(zeroLat
				* DEG2RAD));

	}

	/**
	 * @param result
	 *            0=Latitude, 1=Longitude
	 * @param x
	 *            north direction distance = lat
	 * @param z
	 *            east direction distance = lng
	 */
	public static void calcGPSPos(float[] result, double x, double z,
			double zeroLat, double zeroLng) {
		if (result.length != 2) {
			Log.e(LOG_TAG,
					"Length of passed result array did not have the correct length 2");
			Thread.dumpStack();
		}
		result[1] = (float) (z
				/ (111319.889f * Math.cos(zeroLat * 0.0174532925f)) + zeroLng);
		result[0] = (float) (x / 111133.3333f + zeroLat);
	}

}
