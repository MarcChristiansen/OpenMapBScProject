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
    void setDestination(Node node);
    Node getDestination();
    double getWeight();
}
