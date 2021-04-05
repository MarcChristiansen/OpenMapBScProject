package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;
import org.json.simple.JSONObject;

import java.util.List;

public class NodeImpl extends BaseLineNodeImpl {

    private Node predecessor;
    private double distance;
    private boolean visited;

    /**
     * Create a new node from an id and latitude and longitude (This uses UTM32N
     * @param id The id of the node
     * @param lat The latitude of the node
     * @param lon The longitude of the node
     */
    public NodeImpl(long id, double lat, double lon){
        super(id, lat, lon);

    }

    public NodeImpl(long id, double x, double y, List<Path> pathList){
        super(id, x, y, pathList);

    }

    public NodeImpl(JSONObject obj){
        super(obj);

        this.distance = Double.MAX_VALUE;
        this.predecessor = null;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public Node getPredecessor() {
        return predecessor;
    }

    @Override
    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public boolean getVisited() {
        return visited;
    }

    @Override
    public void setVisited(boolean b) {
        visited = b;
    }
}
