package openmap.special;

import com.fasterxml.jackson.core.JsonGenerator;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.parsing.json.JsonGraphConstants;
import org.apache.commons.lang3.NotImplementedException;
import org.json.simple.JSONObject;

import java.io.IOException;

public class DecodingPathImpl implements Path {
    Long nodeId;
    double weight;

    public DecodingPathImpl(long destinationId, double weight){
        this.nodeId = destinationId;
        this.weight = weight;
    }


    public DecodingPathImpl(JSONObject obj){
        this.nodeId = (Long)obj.get(JsonGraphConstants.PathDestId);
        this.weight = (double)obj.get(JsonGraphConstants.PathWeight);
    }

    @Override
    public Node getDestination() {
        throw new NotImplementedException("Class DecodingPathImpl only used for decoding, method should not be called");
    }

    @Override
    public long getDestinationId() {
        return nodeId;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public JSONObject getJSONObject() {
        throw new NotImplementedException("Class DecodingPathImpl only used for decoding, method should not be called");
    }

    @Override
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException {
        throw new NotImplementedException("Class DecodingPathImpl only used for decoding, method should not be called");
    }
}
