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
        public static MathTransform transformLatLonToUtm32N;
        public static MathTransform transformUtm32NToLatLon;

        public static Coordinate latLonToUtm32N(Coordinate coordinate) {
            Coordinate resCord = new Coordinate(0,0);
            try {
                //First run we ensure we have the transform
                if(transformLatLonToUtm32N == null){
                    //EPSG:4326 is lat/lon
                    transformLatLonToUtm32N  = CRS.findMathTransform(CRS.decode("EPSG:4326"), CRS.decode("EPSG:25832"), false);
                }

                JTS.transform(coordinate, resCord, transformLatLonToUtm32N);


            } catch (Exception e) {
                e.printStackTrace();
            }


            return resCord;
        }

        public static Coordinate utm32NToLatLon(Coordinate coordinate) {
            Coordinate resCord = new Coordinate(0,0);
            try {
                if(transformUtm32NToLatLon == null) {
                    //EPSG:4326 is lat/lon, EPSG:32632 is x, y
                    transformUtm32NToLatLon = CRS.findMathTransform(CRS.decode("EPSG:25832"), CRS.decode("EPSG:4326"), false);
                }

                JTS.transform(coordinate, resCord, transformUtm32NToLatLon);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resCord;
        }
    }
}
