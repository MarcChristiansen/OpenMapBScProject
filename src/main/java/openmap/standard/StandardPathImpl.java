package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;

public class StandardPathImpl implements Path {

    Node destination;
    double weight;

    public StandardPathImpl(Node destination, Double weight){
        this.destination = destination;
        this.weight = weight;
    }


    @Override
    public void setDestination(Node destination) {
        this.destination = destination;
    }

    @Override
    public Node getDestination() {
        return destination;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
