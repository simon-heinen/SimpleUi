package geo;

import java.util.ArrayList;

import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;

public class Location {

	private double longitude;
	private double latitude;
	private double altitude;

	/**
	 * Generates Location with height = 0
	 * 
	 * @param long Longitude
	 * @param long Latitude
	 * 
	 */
	public Location(double lat, double longi) {
		longitude = longi;
		latitude = lat;
		altitude = 0;
	}

	public Location(double lat, double longi, long altitude) {
		this(lat, longi);
		this.altitude = altitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
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

	/**
	 * WORKING approach of calculating bounding box with given Location and
	 * radius
	 * 
	 * @param center
	 * @param radiusInMeters
	 * @return ArrayList with 2 location (max and min coordinates)
	 */
	public static ArrayList<Location> getBBox(Location center,
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
		Location minimal = new Location(min.getLatitude(), min.getLongitude());
		Location maximal = new Location(max.getLatitude(), max.getLongitude());
		ArrayList<Location> temp = new ArrayList<Location>();
		temp.add(minimal);
		temp.add(maximal);
		return temp;

	}

	@Override
	public String toString() {
		return this.getLatitude() + "," + this.getLongitude();
	}

}
