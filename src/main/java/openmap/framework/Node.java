package openmap.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface for nodes in a graph representing a road network.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 06-02-2021
 */
public interface Node {
    /**
     * Returns the id of the node
     * @return id of the node
     */
    public long getId();

    /**
     * Returns the latitude of the node
     * @return latitude of the node
     */
    public double getLat();

    /**
     * Returns the longitude of the node
     * @return longitude of the node
     */
    public double getLon();

    /**
     * Returns the X projection for this node
     * @return X projection of the node
     */
    public double getX();

    /**
     * Returns the Y projection for this node
     * @return Y projection of the node
     */
    public double getY();

    public double getDistance();

    public void setDistance(double distance);

    public double getDistance2();

    public void setDistance2(double distance);

    public Node getPredecessor();

    public Node getPredecessor2();

    public void setPredecessor(Node predecessorId);

    public void setPredecessor2(Node predecessorId);

    public boolean getVisited();

    public void setVisited(boolean b);

    public boolean getVisited2();

    public void setVisited2(boolean b);

    /**
     * Returns a list of the paths starting from this node
     * @return list of the paths starting from this node
     */
    public List<Path> getOutgoingPaths();

    /**
     * Returns a list of the paths ending in this node
     * @return list of the paths ending in this node
     */
    public List<Path> getIncomingPaths();

    /**
     * Add an outgoing path out to the path list
     * @param path The outgoing path
     */
    public void addOutgoingPath(Path path);

    /**
     * Add an incoming path out to the path list
     * @param path The incoming path
     */
    public void addIncomingPath(Path path);


    /**
     * Deserialize paths to ensure they point to the correct nodes
     * @param nodeMap The map of all nodes. Used to get references to nodes from their ids.
     */
    public void convertPathDeserialization(Map<Long, Node> nodeMap);

    /**
     * Return a json object representing this object
     * @return A json object copy of the object
     */
    public JSONObject getJSONObject();

    /**
     * Write a node to a json file using a jGenerator
     * @param jGenerator
     * @throws IOException
     */
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException;

}
