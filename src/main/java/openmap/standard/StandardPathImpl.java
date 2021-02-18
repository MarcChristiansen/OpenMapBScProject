package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Map;

public class StandardPathImpl implements Path, Serializable {

    private static final String  jDestId = "destId";
    private static final String  jWeight = "weight";

    Node destination;
    Long nodeId;
    double weight;

    public StandardPathImpl(Node destination, Double weight){
        this.destination = destination;
        this.nodeId = destination.getId();
        this.weight = weight;
    }

    public StandardPathImpl(JSONObject obj){
        this.nodeId = (Long)obj.get(jDestId);
        this.weight = (double)obj.get(jWeight);
    }

    @Override
    public Node getDestination() {
        return destination;
    }

    @Override
    public long getDestinationId() {
        return destination.getId();
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void prepareForSerialization() {
        destination = null;
    }

    @Override
    public void doDeserialization(Map<Long, Node> nodeMap) {
        destination = nodeMap.get(nodeId);
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();

        obj.put(jDestId, nodeId);
        obj.put(jWeight, weight);

        return obj;
    }

    @Override
    public String toString() {
        return "StandardPathImpl{" +
                "destination=" + destination.getId() +
                ", weight=" + weight +
                '}';
    }
}
