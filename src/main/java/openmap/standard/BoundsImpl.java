package openmap.standard;

import openmap.framework.Bounds;

import java.io.Serializable;

public class BoundsImpl implements Bounds, Serializable {
    double minLat, minLon, maxLat, maxLon;

    public BoundsImpl(double minLat, double minLon, double maxLat, double maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLon() {
        return maxLon;
    }
}
