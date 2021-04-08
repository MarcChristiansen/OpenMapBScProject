package openmap.utility;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;

public class CoordinateUtility {
    public static class CoordinateConversion {
        public static MathTransform transformLatLonToUtm32N;
        public static MathTransform transformUtm32NToLatLon;

        public static String wktLatLonToUtm32N = "CONCAT_MT[PARAM_MT[\"Affine\", \n" +
                "    PARAMETER[\"num_row\", 3], \n" +
                "    PARAMETER[\"num_col\", 3], \n" +
                "    PARAMETER[\"elt_0_0\", 0.0], \n" +
                "    PARAMETER[\"elt_0_1\", 1.0], \n" +
                "    PARAMETER[\"elt_1_0\", 1.0], \n" +
                "    PARAMETER[\"elt_1_1\", 0.0]], \n" +
                "  PARAM_MT[\"Ellipsoid_To_Geocentric\", \n" +
                "    PARAMETER[\"dim\", 2], \n" +
                "    PARAMETER[\"semi_major\", 6378137.0], \n" +
                "    PARAMETER[\"semi_minor\", 6356752.314245179]], \n" +
                "  PARAM_MT[\"Geocentric_To_Ellipsoid\", \n" +
                "    PARAMETER[\"dim\", 2], \n" +
                "    PARAMETER[\"semi_major\", 6378137.0], \n" +
                "    PARAMETER[\"semi_minor\", 6356752.314140356]], \n" +
                "  PARAM_MT[\"Transverse_Mercator\", \n" +
                "    PARAMETER[\"semi_major\", 6378137.0], \n" +
                "    PARAMETER[\"semi_minor\", 6356752.314140356], \n" +
                "    PARAMETER[\"central_meridian\", 9.0], \n" +
                "    PARAMETER[\"latitude_of_origin\", 0.0], \n" +
                "    PARAMETER[\"scale_factor\", 0.9996], \n" +
                "    PARAMETER[\"false_easting\", 500000.0], \n" +
                "    PARAMETER[\"false_northing\", 0.0]]]";
        public static String wktUtm32NToLatLon = "CONCAT_MT[INVERSE_MT[PARAM_MT[\"Transverse_Mercator\", \n" +
                "      PARAMETER[\"semi_major\", 6378137.0], \n" +
                "      PARAMETER[\"semi_minor\", 6356752.314140356], \n" +
                "      PARAMETER[\"central_meridian\", 9.0], \n" +
                "      PARAMETER[\"latitude_of_origin\", 0.0], \n" +
                "      PARAMETER[\"scale_factor\", 0.9996], \n" +
                "      PARAMETER[\"false_easting\", 500000.0], \n" +
                "      PARAMETER[\"false_northing\", 0.0]]], \n" +
                "  PARAM_MT[\"Ellipsoid_To_Geocentric\", \n" +
                "    PARAMETER[\"dim\", 2], \n" +
                "    PARAMETER[\"semi_major\", 6378137.0], \n" +
                "    PARAMETER[\"semi_minor\", 6356752.314140356]], \n" +
                "  PARAM_MT[\"Geocentric_To_Ellipsoid\", \n" +
                "    PARAMETER[\"dim\", 2], \n" +
                "    PARAMETER[\"semi_major\", 6378137.0], \n" +
                "    PARAMETER[\"semi_minor\", 6356752.314245179]], \n" +
                "  PARAM_MT[\"Affine\", \n" +
                "    PARAMETER[\"num_row\", 3], \n" +
                "    PARAMETER[\"num_col\", 3], \n" +
                "    PARAMETER[\"elt_0_0\", 0.0], \n" +
                "    PARAMETER[\"elt_0_1\", 1.0], \n" +
                "    PARAMETER[\"elt_1_0\", 1.0], \n" +
                "    PARAMETER[\"elt_1_1\", 0.0]]]\n" +
                "\n" +
                "Process finished with exit code 0\n";
        public static Coordinate latLonToUtm32N(Coordinate coordinate) {
            Coordinate resCord = new Coordinate(0,0);
            try {
                //First run we ensure we have the transform
                if(transformLatLonToUtm32N == null){
                    //EPSG:4326 is lat/lon

                    MathTransformFactory mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
                    transformLatLonToUtm32N = mtFactory.createFromWKT(wktLatLonToUtm32N);
                    /*
                    CoordinateReferenceSystem source = CRS.decode("EPSG:4326");
                    CoordinateReferenceSystem target = CRS.decode("EPSG:25832");
                    transformLatLonToUtm32N  = CRS.findMathTransform(source, target, false);
                    System.out.println(transformLatLonToUtm32N.toWKT());*/
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
                    MathTransformFactory mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
                    transformUtm32NToLatLon = mtFactory.createFromWKT(wktUtm32NToLatLon);
                }

                JTS.transform(coordinate, resCord, transformUtm32NToLatLon);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resCord;
        }
    }
}
