package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard node implementation extending baseline node.
 * Also contains a lot of different fields for use in pathfinding to avoid excessive use of maps
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 09-02-2021
 */
public class NodeImpl extends BaseLineNodeImpl {

    private Node predecessor;
    private double distance;
    private boolean visited;
    private List<Path> incomingPaths;

    /**
     * Create a new node from an id and latitude and longitude (This uses UTM32N
     * @param id The id of the node
     * @param lat The latitude of the node
     * @param lon The longitude of the node
     */
    public NodeImpl(long id, double lat, double lon){
        super(id, lat, lon);
        incomingPaths = new ArrayList<>();

    }

    public NodeImpl(long id, double x, double y, List<Path> pathList){
        super(id, x, y, pathList);

        //Handle incoming paths
        incomingPaths = new ArrayList<>();

    }

    public NodeImpl(JSONObject obj){
        super(obj);

        incomingPaths = new ArrayList<>();
        this.distance = Double.MAX_VALUE;
        this.predecessor = null;
    }

    @Override
    public void addOutgoingPath(Path p){
        super.addOutgoingPath(p);
        p.getSource().addIncomingPath(p);
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

    @Override
    public List<Path> getIncomingPaths() {
        return incomingPaths;
    }

    @Override
    public void addIncomingPath(Path path) {
        incomingPaths.add(path);
    }
}
