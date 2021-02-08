package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;

import java.io.Serializable;

public class StandardPathImpl implements Path, Serializable {

    Node destination;
    double weight;

    public StandardPathImpl(Node destination, Double weight){
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public Node getDestination() {
        return destination;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "StandardPathImpl{" +
                "destination=" + destination.getId() +
                ", weight=" + weight +
                '}';
    }
}
