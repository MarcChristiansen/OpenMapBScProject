package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;

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
    public String toString() {
        return "StandardPathImpl{" +
                "destination=" + destination.getId() +
                ", weight=" + weight +
                '}';
    }
}
