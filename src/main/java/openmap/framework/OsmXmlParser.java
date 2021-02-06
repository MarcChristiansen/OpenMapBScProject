package openmap.framework;
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
     * Parses the XML file
     * @param fileIn the name of the XML file to parse
     */
    public void parse(String fileIn);
}
