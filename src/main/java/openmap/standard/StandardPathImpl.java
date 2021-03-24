package openmap.standard;

import com.fasterxml.jackson.core.JsonGenerator;
import openmap.parsing.json.JsonGraphConstants;
import openmap.framework.Node;
import openmap.framework.Path;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class StandardPathImpl implements Path, Serializable {


    Node destination;
    Long nodeId;
    double weight;

    public StandardPathImpl(Node destination, Double weight){
        this.destination = destination;
        this.nodeId = destination.getId();
        this.weight = weight;
    }

    public StandardPathImpl(long destinationId, double weight){
        this.nodeId = destinationId;
        this.weight = weight;
    }

    public StandardPathImpl(JSONObject obj){
        this.nodeId = (Long)obj.get(JsonGraphConstants.PathDestId);
        this.weight = (double)obj.get(JsonGraphConstants.PathWeight);
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
    public void doDeserialization(Map<Long, Node> nodeMap) {
        destination = nodeMap.get(nodeId);
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();

        obj.put(JsonGraphConstants.PathDestId, destination.getId());
        obj.put(JsonGraphConstants.PathWeight, weight);

        return obj;
    }

    @Override
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException {
        jGenerator.writeStartObject();
        jGenerator.writeNumberField(JsonGraphConstants.PathDestId, nodeId);
        jGenerator.writeNumberField(JsonGraphConstants.PathWeight, weight);
        jGenerator.writeEndObject();

    }

    @Override
    public String toString() {
        return "StandardPathImpl{" +
                "destination=" + destination.getId() +
                ", weight=" + weight +
                '}';
    }


}
