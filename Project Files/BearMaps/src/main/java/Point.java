/**
 * Created by samrstarks on 4/6/16.
 */
public class Point {
    private double lat, lon;

    public Point(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    @Override
    public String toString() {
        return "Point{"
                + "lat=" + lat
                + ", lon=" + lon
                + '}';
    }
}
