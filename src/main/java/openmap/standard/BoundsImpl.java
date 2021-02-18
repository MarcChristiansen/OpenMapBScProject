package openmap.standard;

import openmap.framework.Bounds;
import openmap.utility.CoordinateUtility;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.Coordinate;

import java.io.Serializable;

public class BoundsImpl implements Bounds, Serializable {

    private static final String  jMinX = "jMinX";
    private static final String  jMinY = "jMinY";
    private static final String  jMaxX = "jMaxX";
    private static final String  jMaxY = "jMaxY";

    Coordinate minCoordinate, maxCoordinate;

    public BoundsImpl(double minLat, double minLon, double maxLat, double maxLon) {
        minCoordinate = new Coordinate(minLat, minLon);
        maxCoordinate = new Coordinate(maxLat, maxLon);
        minCoordinate = CoordinateUtility.CoordinateConversion.latLonToUtm32N(minCoordinate);
        maxCoordinate = CoordinateUtility.CoordinateConversion.latLonToUtm32N(maxCoordinate);
    }

    public BoundsImpl(JSONObject obj){
        minCoordinate = new Coordinate((Double)obj.get(jMinX), (Double)obj.get(jMinY));
        maxCoordinate = new Coordinate((Double)obj.get(jMaxX), (Double)obj.get(jMaxY));
    }

    public double getMinLat() {
        return CoordinateUtility.CoordinateConversion.utm32NToLatLon(minCoordinate).x;
    }

    public double getMinLon() {
        return CoordinateUtility.CoordinateConversion.utm32NToLatLon(minCoordinate).y;
    }

    public double getMaxLat() {
        return CoordinateUtility.CoordinateConversion.utm32NToLatLon(maxCoordinate).x;
    }

    public double getMaxLon() {
        return CoordinateUtility.CoordinateConversion.utm32NToLatLon(maxCoordinate).y;
    }

    @Override
    public double getMinX() {
        return minCoordinate.x;
    }

    @Override
    public double getMaxX() {
        return maxCoordinate.x;
    }

    @Override
    public double getMinY() {
        return minCoordinate.y;
    }

    @Override
    public double getMaxY() {
        return maxCoordinate.y;
    }

    @Override
    public void setMinX(double v) {
        minCoordinate.x = v;
    }

    @Override
    public void setMaxX(double v) {
        maxCoordinate.x = v;
    }

    @Override
    public void setMinY(double v) {
        minCoordinate.y = v;
    }

    @Override
    public void setMaxY(double v) {
        maxCoordinate.y = v;
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(jMinX, minCoordinate.x);
        obj.put(jMinY, minCoordinate.y);
        obj.put(jMaxX, maxCoordinate.x);
        obj.put(jMaxY, maxCoordinate.y);

        return obj;
    }


}
