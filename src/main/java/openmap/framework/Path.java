package openmap.framework;

public interface Path {
    void setDestination(Node node);
    Node getDestination();
    double getWeight();
}
