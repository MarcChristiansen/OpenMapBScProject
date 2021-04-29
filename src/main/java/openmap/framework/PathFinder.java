package openmap.framework;

import openmap.gui.NodeDrawingInfo;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Interface for paths in a graph representing a road network.
 * Paths connect two nodes in the graph
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.1
 * @since 015-04-2021
 */
public interface PathFinder {
    /**
     * Return the shortest path from source to destination, in the form of a list of the node ids, of every node in the path.
     * In case there is no possible path between source and destination, return a list with only one value that being -1
     * @param source the id of the source node
     * @param destination the id of the destination node
     * @return a list of nodes of all the nodes in the shortest path
     */
    public List<Node> getShortestPath(Node source, Node destination);

    /**
     * Get last execution time of the algorithm.
     * @return Last execution time of the algorithm
     */
    public long getLastExecutionTime();

    /**
     * Function to test if given node was visited during run of this pathfinder.
     * @return Function to be used to see if node should be drawn and what color
     */
    public Function<Node, NodeDrawingInfo> getVisitedCheckFunction();

    /**
     * Function to return a List with the indices of "to" landmarks used
     * @return a list of indices of landmarks used
     */
    public List<Integer> getLandmarksUsedTo();

    /**
     * Function to return a List with the indices of "from" landmarks used
     * @return a list of indices of landmarks used
     */
    public List<Integer> getLandmarksUsedFrom();

    /**
     * Set the amount of landmarks to be used on passes.
     * @param i
     */
    public void SetLandmarkSubsetSize(int i);
}
