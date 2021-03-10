package openmap.framework;

import java.util.List;
import java.util.Map;

/**
 * Interface for Parser for the Open street map XML format.
 * Used to parse the OSM xml into the format used by the graph.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.1
 * @since 09-02-2021
 */
public interface OsmParser {

    /**
     * Parses all ways that are in some way a path
     * @return A list of all ways parsed in a OSM representation
     */
    public List<OsmWay> parseWays();

    /**
     * Parses all nodes with a value on their id >1
     * @param nodeWayCounter Map that maps from nodeId to an integer of how many ways it participates in //Todo try and refactor this away
     * @return A map that maps from ids in nodes to the actual node.
     */
    public Map<Long, Node> parseNodes(Map<Long, Integer> nodeWayCounter);

    /**
     * Parse the bounds of the given OSM file into a bounds object
     * @return a bounds object holding the bounds of the graph.
     */
    public Bounds parseBounds();
}
