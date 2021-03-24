package openmap.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;

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
     * Return the destination node
     * @return The destination node
     */
    Node getDestination(); //TODO should this be an id for a dest node

    /**
     * Return the destination id
     * @return The destination id
     */
    long getDestinationId(); //TODO should this be an id for a dest node

    /**
     * returns the weight of this path
     * @return the weight of this path
     */
    double getWeight();

    /**
     * Add references to other objects again through a known map of all nodes.
     * @param nodeMap a map that maps node ids to nodes
     */
    void doDeserialization(Map<Long, Node> nodeMap);

    /**
     * Return a json object representing this object
     * @return A json object copy of the object
     */
    public JSONObject getJSONObject();

    /**
     * Write path to Json file using a json generator
     * @return A json object copy of the object
     */
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException;
}
