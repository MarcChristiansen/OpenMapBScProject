package openmap.runnable;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.IOException;

public class WKTCreation {
    public static void main(String[] args) throws FactoryException {
        CoordinateReferenceSystem source = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem target = CRS.decode("EPSG:25832");
        MathTransform transform= CRS.findMathTransform(source, target, false);
        System.out.println(transform.toWKT());

        System.out.println();
        System.out.println("UTM32N to Lat/Lon");
        System.out.println();

        source = CRS.decode("EPSG:25832");
        target = CRS.decode("EPSG:4326");
        transform = CRS.findMathTransform(source, target, false);
        System.out.println(transform.toWKT());

    }
}
