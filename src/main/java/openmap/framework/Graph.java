package openmap.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Interface that represents a graph, contains a map of all the nodes, and the bounds of the graph
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 09-02-2021
 */
public interface Graph {

    /**
     * Get the node map used to reference all nodes
     * @return The node map
     */
    public Map<Long, Node> getNodeMap();

    /**
     * Get the bounds object of this graph
     * @return The bounds object of the graph
     */
    public Bounds getBounds();

    /**
     * Run any needed deserialization steps
     */
    public void doDeserialization();

    /**
     * Get a Json object fully representing this graph
     * @return A json object representing this graph
     */
    public JSONObject getJSONObject();

    /**
     * Write graph to a stream json encoder
     * @param jGenerator The generator to write to
     * @throws IOException Possible IOException if something goes wrong while writing
     */
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException;

}
