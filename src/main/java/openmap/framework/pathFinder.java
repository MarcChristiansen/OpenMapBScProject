package openmap.framework;

import java.util.List;

/**
 * Interface for paths in a graph representing a road network.
 * Paths connect two nodes in the graph
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 017-02-2021
 */
public interface pathFinder {
    /**
     * Return the shortest path from source to destination, in the form of a list of the node ids, of every node in the path.
     * In case there is no possible path between source and destination, return a list with only one value that being -1
     * @param source the id of the source node
     * @param destination the id of the destination node
     * @return a list of ids of all the nodes in the shortest path
     */
    public List<Long> getShortestPath(Long source, Long destination);
}
