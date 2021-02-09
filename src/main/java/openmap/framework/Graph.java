package openmap.framework;

import java.util.Map;

/**
 * Interface that represents a graph, contains a map of all the nodes, and the bounds of the graph
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 09-02-2021
 */
public interface Graph {

    public Map<Long, Node> getNodeMap();

    public Bounds getBounds();

    public void prepareForSerialization();

    public void doDeserialization();

}
