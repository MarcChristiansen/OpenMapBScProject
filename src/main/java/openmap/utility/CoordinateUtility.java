package openmap.utility;

import org.geotools.geometry.jts.CompoundRing;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.operation.MathTransform;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CoordinateUtility {
    public static class CoordinateConversion {
        public static Coordinate latLonToUtm32N(Coordinate coordinate) {
            Coordinate resCord = new Coordinate(0,0);
            try {
                //EPSG:4326 is lat/lon //TODO CONFIRM THIS <-, EPSG:32632 is x, y
                MathTransform transform = CRS.findMathTransform(CRS.decode("EPSG:4326"), CRS.decode("EPSG:25832"), false);

                JTS.transform(coordinate, resCord, transform);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resCord;
        }

        public static Coordinate utm32NToLatLon(Coordinate coordinate) {
            Coordinate resCord = new Coordinate(0,0);
            try {
                //EPSG:4326 is lon/lat, EPSG:32632 is x, y
                MathTransform transform = CRS.findMathTransform(CRS.decode("EPSG:25832"), CRS.decode("EPSG:4326"), false);

                JTS.transform(coordinate, resCord, transform);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resCord;
        }
    }
}
