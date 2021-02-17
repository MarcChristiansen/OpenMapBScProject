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
    public Node getNode();
    public Double getDist();
}
