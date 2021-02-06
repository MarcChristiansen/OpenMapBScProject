package openmap.framework;

import java.util.List;
import java.util.Map;

/**
 * Interface for Parser for the Open street map XML format.
 * Used to parse the OSM xml into the format used by the graph.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 06-02-2021
 */
public interface OsmXmlParser {

    /**
     * Parses all ways that are in some way a path
     * @param fileIn The file path to use
     * @return A list of all ways parsed in a OSM representation
     */
    public List<OsmWay> parseWays(String fileIn);

    /**
     * Parses all nodes with a value on their id >1
     * @param fileIn The file path to use
     * @param nodeWayCounter Map that maps from nodeId to an integer of how many ways it participates in
     * @return A map that maps from ids in nodes to the actual node.
     */
    public Map<Long, Node> parseNodes(String fileIn, Map<Long, Integer> nodeWayCounter);
}
