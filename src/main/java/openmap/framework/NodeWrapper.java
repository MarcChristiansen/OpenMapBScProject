package openmap.framework;

/**
 * A wrapper interface for nodes, for use with dijkstra containing a node and the distance to said node.
 * Used for the priority queue for dijkstra.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 017-02-2021
 */
public interface NodeWrapper {

    /**
     * Get the node contained in this wrapper
     * @return Node in wrapper
     */
    public Node getNode();

    /**
     * Get the distance associated with the node in the wrapper
     * @return Distance associated with the node in the wrapper
     */
    public double getDist();
}
