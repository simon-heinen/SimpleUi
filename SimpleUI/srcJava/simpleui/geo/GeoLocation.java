package simpleui.geo;

import java.io.Serializable;
import java.util.ArrayList;

import simpleui.util.Log;
import simpleui.util.Vec;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;

public class GeoLocation implements Serializable {

	private static final String LOG_TAG = "GeoLocation";

	private static GeoLocation zeroPos;

	private double longitude;
	private double latitude;
	private double altitude;

	transient private Vec myVec;

	/**
	 * Generates Location with height = 0
	 * 
	 * @param long Longitude
	 * @param long Latitude
	 * 
	 */
	public GeoLocation(double lat, double longi) {
		longitude = longi;
		latitude = lat;
		altitude = 0;
	}

	public GeoLocation(double lat, double longi, double altitude) {
		this(lat, longi);
		this.altitude = altitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		myVec = null;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
		myVec = null;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public String calculateGeoHash(int nrOfBits) {
		return calculateGeoHash(this.latitude, this.longitude, nrOfBits);
	}

	public double getDistanceTo(GeoLocation b) {
		return getDistanceTo(this.toVec(), b.toVec());
	}

	/**
	 * WORKING approach of calculating bounding box with given Location and
	 * radius
	 * 
	 * @param center
	 * @param radiusInMeters
	 * @return ArrayList with 2 location (max and min coordinates)
	 */
	public static ArrayList<GeoLocation> getBBox(GeoLocation center,
			int radiusInMeters) {
		WGS84Point point = new WGS84Point(center.getLatitude(),
				center.getLongitude());
		WGS84Point point1 = VincentyGeodesy.moveInDirection(point, 135,
				radiusInMeters);
		WGS84Point point2 = VincentyGeodesy.moveInDirection(point, 315,
				radiusInMeters);

		WGS84Point min, max;
		if (point1.getLatitude() > point2.getLatitude()) {
			if (point1.getLongitude() > point2.getLongitude()) {
				max = point1;
				min = point2;
			} else {
				max = new WGS84Point(point1.getLatitude(),
						point2.getLongitude());
				min = new WGS84Point(point2.getLatitude(),
						point1.getLongitude());
			}
		} else {
			if (point1.getLongitude() > point2.getLongitude()) {
				max = new WGS84Point(point2.getLatitude(),
						point1.getLongitude());
				min = new WGS84Point(point1.getLatitude(),
						point2.getLongitude());
			} else {
				max = point2;
				min = point1;
			}
		}
		GeoLocation minimal = new GeoLocation(min.getLatitude(),
				min.getLongitude());
		GeoLocation maximal = new GeoLocation(max.getLatitude(),
				max.getLongitude());
		ArrayList<GeoLocation> temp = new ArrayList<GeoLocation>();
		temp.add(minimal);
		temp.add(maximal);
		return temp;

	}

	/**
	 * @return north in meters (X AXIS)
	 */
	public float getX() {
		return toVec().x;
	}

	/**
	 * @return east in meters (Z AXIS)
	 */
	public float getZ() {
		return toVec().z;
	}

	/**
	 * @return vec.x=north in meters (X AXIS), vec.z=east in meters (Z AXIS)
	 */
	public Vec toVec() {
		calcVecIfNeeded();
		return myVec.copy();
	}

	private void calcVecIfNeeded() {
		if (myVec == null) {
			if (zeroPos == null) {
				// The first GeoLocation which uses the virtual pos concept
				// defines the zeroPos
				setZeroPos(this.copy());
			}
			float[] result = new float[3];
			CoordRemap.calcVirtualPos(result, latitude, longitude,
					zeroPos.getLatitude(), zeroPos.getLongitude());
			myVec = new Vec(result[2], 0, result[0]);
		}
	}

	public GeoLocation copy() {
		return new GeoLocation(latitude, longitude, altitude);
	}

	/**
	 * In DroidAR all coordinates have to be decimal degrees. Use this method if
	 * you have to convert to decimal degrees.
	 * 
	 * Example usage: <br>
	 * 16� 19' 28,29" to 16,324525�
	 * 
	 * @param degree
	 *            16
	 * @param minutes
	 *            19
	 * @param seconds
	 *            28,29
	 * @return 16,324525�
	 */
	public static double convertDegreesMinutesSecondsToDecimalDegrees(
			double degree, double minutes, double seconds) {
		return degree + ((minutes + (seconds / 60)) / 60) / 60;
	}

	public static GeoLocation createRandomLocation(GeoLocation center,
			float minDistanceInMeters, float maxDistanceInMeters) {
		Vec virtPos = Vec.getNewRandomPosInXYPlane(new Vec(),
				minDistanceInMeters, maxDistanceInMeters);
		virtPos.x += center.getX();
		virtPos.y += center.getZ();
		return GeoLocation.newGeoLocationFromRelativePos(virtPos.y, virtPos.x);
	}

	public static GeoLocation createRandomLocation(GeoLocation center,
			float maxDistanceInMeters) {
		return createRandomLocation(center, 0, maxDistanceInMeters);
	}

	public static GeoLocation getZeroPos() {
		return zeroPos;
	}

	public static void setZeroPos(GeoLocation zeroPos) {
		if (GeoLocation.zeroPos == null) {
			GeoLocation.zeroPos = zeroPos;
		} else {
			Log.e(LOG_TAG, "Zero pos already set, will not accept new one");
		}
	}

	public static GeoLocation toGeoLocation(Vec relativePos) {
		return newGeoLocationFromRelativePos(relativePos.z, relativePos.x);
	}

	public static GeoLocation newGeoLocationFromRelativePos(float z, float x) {

		// see
		// http://stackoverflow.com/questions/7386286/how-to-generate-random-lat-lng-inside-an-area-given-the-center-and-the-radius
		// for more info on how this works
		// Only accurate for radius < 100000m
		// TODO: Fix this:
		// http://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates

		if (zeroPos == null) {
			Log.e(LOG_TAG, "Cant calc virtual pos, no zero pos set yet!");
			return null;
		}
		float[] longiAndLati = new float[2];
		CoordRemap.calcGPSPos(longiAndLati, z, x, zeroPos.getLatitude(),
				zeroPos.getLongitude());
		return new GeoLocation(longiAndLati[0], longiAndLati[1]);
	}

	public static double getDistanceTo(GeoLocation a, GeoLocation b) {
		return getDistanceTo(a.toVec(), b.toVec());
	}

	private static double getDistanceTo(Vec a, Vec b) {
		Vec l = Vec.sub(a, b);
		return l.getLength();
	}

	/**
	 * instead use {@link GeoLocation#getDistanceTo(GeoLocation, GeoLocation)}
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return distance in meter
	 * @throws Exception
	 *             when latitude or longitude is not a number
	 */
	@Deprecated
	public static double getDistanceTo(double lat1, double lon1, double lat2,
			double lon2) {
		// copied from c# GetDistanceTo
		if (Double.isNaN(lat1) || Double.isNaN(lon1) || Double.isNaN(lat2)
				|| Double.isNaN(lon2)) {
			Log.w("GeoLocation.java.getDistanceTo received NaN values error");
			return Double.NaN;
		} else {
			double latitude = lat1 * 0.0174532925199433;
			double longitude = lon1 * 0.0174532925199433;
			double num = lat2 * 0.0174532925199433;
			double longitude1 = lon2 * 0.0174532925199433;
			double num1 = longitude1 - longitude;
			double num2 = num - latitude;
			double num3 = Math.pow(Math.sin(num2 / 2), 2) + Math.cos(latitude)
					* Math.cos(num) * Math.pow(Math.sin(num1 / 2), 2);
			double num4 = 2 * Math.atan2(Math.sqrt(num3), Math.sqrt(1 - num3));
			double num5 = 6376500 * num4;
			return num5;
		}
	}

	@Override
	public String toString() {
		if (altitude != 0) {
			return "(" + latitude + "," + longitude + ") with height="
					+ altitude;
		}
		return "(" + latitude + "," + longitude + ")";
	}

	public static String calculateGeoHash(double lat, double lon, int nrOfBits) {
		String ret = "";
		try {
			ret = GeoHash.withBitPrecision(lat, lon, nrOfBits).toBinaryString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public double getNorthClockwiseAngleOf(GeoLocation b) {
		float[] result = new float[3];
		CoordRemap.calcVirtualPos(result, b.latitude, b.longitude, latitude,
				longitude);
		return (90 + 360 - Vec.getRotationAroundZAxis(result[2], result[0])) % 360;
	}

	public double getAltitude() {
		return altitude;
	}

}
