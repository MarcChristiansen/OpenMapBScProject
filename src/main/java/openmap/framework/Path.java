package openmap.framework;
/**
 * Interface for paths in a graph representing a road network.
 * Paths connect two nodes in the graph
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 06-02-2021
 */
public interface Path {
    /**
     * Return
     * @return
     */
    Node getDestination(); //TODO should this be an id for a dest node

    /**
     * returns the weight of this path
     * @return the weight of this path
     */
    double getWeight();
}
