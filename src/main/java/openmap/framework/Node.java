package openmap.framework;

import java.util.List;

/**
 * Interface for nodes in a graph representing a road network.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 06-02-2021
 */
public interface Node {
    /**
     * Returns the id of the node
     * @return id of the node
     */
    public long getId();

    /**
     * Returns the latitude of the node
     * @return latitude of the node
     */
    public double getLat();

    /**
     * Returns the longitude of the node
     * @return longitude of the node
     */
    public double getLon();

    /**
     * Returns a list of the paths starting from this node
     * @return list of the paths starting from this node
     */
    public List<Path> getPaths();

    /**
     * Add a path out to the path list
     * @param path
     */
    public void addPath(Path path);

}
