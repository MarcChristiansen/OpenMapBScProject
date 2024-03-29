package openmap.standard;

import com.fasterxml.jackson.core.JsonGenerator;
import openmap.parsing.json.JsonGraphConstants;
import openmap.framework.Node;
import openmap.framework.Path;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Baseline path implementation
 *
 * Includes both a source and a destination for use with bidirectional algorithms
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 9-02-2021
 */
public class StandardPathImpl implements Path, Serializable {

    Node destination;
    Node source;
    float weight;


    public StandardPathImpl(Node destination, Node source, double weight){
        this.destination = destination;
        this.weight = (float)weight;
        this.source = source;

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
    public Node getSource() {
        return source;
    }

    @Override
    public long getSourceId() {
        return source.getId();
    }

    @Override
    public double getWeight() {
        return weight;
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
        jGenerator.writeNumberField(JsonGraphConstants.PathDestId, destination.getId());
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
