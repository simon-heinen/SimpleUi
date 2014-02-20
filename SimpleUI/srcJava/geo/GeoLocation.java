package geo;

import math.Vec;
import util.Log;
import ch.hsr.geohash.GeoHash;

public class GeoLocation {

	public GeoLocation() {
		latitude = 0;
		longitude = 0;
	}

	public GeoLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	private double latitude;
	private double longitude;

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String calculateGeoHash(int nrOfBits) {
		return calculateGeoHash(this.latitude, this.longitude, nrOfBits);
	}

	public double getDistanceTo(GeoLocation g) {
		return getDistanceTo(this.latitude, this.longitude, g.latitude,
				g.longitude);
	}

	/**
	 * In DroidAR all coordinates have to be decimal degrees. Use this method if
	 * you have to convert to decimal degrees.
	 * 
	 * Example usage: <br>
	 * 16° 19' 28,29" to 16,324525°
	 * 
	 * @param degree
	 *            16
	 * @param minutes
	 *            19
	 * @param seconds
	 *            28,29
	 * @return 16,324525°
	 */
	public static double convertDegreesMinutesSecondsToDecimalDegrees(
			double degree, double minutes, double seconds) {
		return degree + ((minutes + (seconds / 60)) / 60) / 60;
	}

	// see
	// http://stackoverflow.com/questions/7386286/how-to-generate-random-lat-lng-inside-an-area-given-the-center-and-the-radius
	// for more info on how this works
	// Only accurate for radius < 100000m
	// TODO: Fix this:
	// http://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
	public static GeoLocation createRandomLocation(GeoLocation center,
			float radius) {
		Vec v = Vec.getNewRandomPosInXYPlane(new Vec(), 0, radius);
		Vec randomGpsPos = GeoLocation.toGPSPosition(v, center.latitude,
				center.longitude);
		return new GeoLocation(randomGpsPos.y, randomGpsPos.x);
	}

	public static Vec toVirtualPos(double userLatitude, double userLongitude,
			double zeroLatitude, double zeroLongitude) {
		Vec position = new Vec();
		position.x = (float) ((userLongitude - zeroLongitude) * 111319.4917 * Math
				.cos(zeroLatitude * 0.0174532925));
		position.y = (float) ((userLatitude - zeroLatitude) * 111133.3333);
		return position;
	}

	/**
	 * @param zeroLatitude
	 * @param zeroLongitude
	 * @param zeroAltitude
	 * @return a Vector with x=Longitude, y=Latitude
	 */
	public static Vec toGPSPosition(Vec virtualPosition, double zeroLatitude,
			double zeroLongitude) {
		if (virtualPosition != null) {
			/*
			 * same formula as in calcVirtualPos() but resolved for latitude and
			 * longitude:
			 */
			Vec result = new Vec();
			result.x = (float) (virtualPosition.x
					/ (111319.889f * Math.cos(zeroLatitude * 0.0174532925f)) + zeroLongitude);
			result.y = (float) (virtualPosition.y / 111133.3333f + zeroLatitude);
			result.z = (virtualPosition.z);
			return result;
		}
		return null;
	}

	// copied from c# GetDistanceTo
	/**
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return distance in meter
	 * @throws Exception
	 *             when latitude or longitude is not a number
	 */
	public static double getDistanceTo(double lat1, double lon1, double lat2,
			double lon2) {
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
		// TODO Auto-generated method stub
		return "GeoLocation: " + latitude + "," + longitude;
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

		Vec v = toVirtualPos(b.latitude, b.longitude, latitude, longitude);

		// System.out.println(v.x);
		// System.out.println(v.y);

		return (90 + 360 - Vec.getRotationAroundZAxis(v.x, v.y)) % 360;
	}

}
