package openmap.framework;

/**
 * Interface that represents the bounds of a graph.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 09-02-2021
 */
public interface Bounds {
    public double getMinLat();
    public double getMaxLat();

    public double getMinLon();
    public double getMaxLon();

}