package openmap.standard;

import openmap.JsonParsing.JsonGraphConstants;
import openmap.framework.Bounds;
import openmap.utility.CoordinateUtility;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.Coordinate;

import java.io.Serializable;

public class BoundsImpl implements Bounds, Serializable {

    Coordinate minCoordinate, maxCoordinate;

    public BoundsImpl(double minLat, double minLon, double maxLat, double maxLon) {
        minCoordinate = new Coordinate(minLat, minLon);
        maxCoordinate = new Coordinate(maxLat, maxLon);
        minCoordinate = CoordinateUtility.CoordinateConversion.latLonToUtm32N(minCoordinate);
        maxCoordinate = CoordinateUtility.CoordinateConversion.latLonToUtm32N(maxCoordinate);
    }

    public BoundsImpl(){
        minCoordinate = new Coordinate(0, 0);
        maxCoordinate = new Coordinate(0, 0);
    }

    public BoundsImpl(JSONObject obj){
        minCoordinate = new Coordinate((Double)obj.get(JsonGraphConstants.BoundsMinX), (Double)obj.get(JsonGraphConstants.BoundsMinY));
        maxCoordinate = new Coordinate((Double)obj.get(JsonGraphConstants.BoundsMaxX), (Double)obj.get(JsonGraphConstants.BoundsMaxY));
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
        obj.put(JsonGraphConstants.BoundsMinX, minCoordinate.x);
        obj.put(JsonGraphConstants.BoundsMinY, minCoordinate.y);
        obj.put(JsonGraphConstants.BoundsMaxX, maxCoordinate.x);
        obj.put(JsonGraphConstants.BoundsMaxY, maxCoordinate.y);

        return obj;
    }


}
