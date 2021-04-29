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
    private Node predecessor2;
    private double distance;
    private double distance2;
    private boolean visited;
    private boolean visited2;
    private List<Path> incomingPaths;
    private List<Double> landmarkDistancesFromLandmark;
    private List<Double> landmarkDistancesToLandmark;


    /**
     * Create a new node from an id and latitude and longitude (This uses UTM32N
     * @param id The id of the node
     * @param lat The latitude of the node
     * @param lon The longitude of the node
     */
    public NodeImpl(long id, double lat, double lon){
        super(id, lat, lon);
        init();
    }



    public NodeImpl(long id, double x, double y, List<Path> pathList){
        super(id, x, y, pathList);

        //Handle incoming paths
        init();

    }

    public NodeImpl(JSONObject obj){
        super(obj);

        init();
        this.distance = Double.MAX_VALUE;
        this.distance2 = Double.MAX_VALUE;
        this.predecessor = null;
        this.predecessor2 = null;
    }

    private void init() {
        incomingPaths = new ArrayList<>();
        landmarkDistancesFromLandmark = new ArrayList<>();
        landmarkDistancesToLandmark = new ArrayList<>();
    }

    @Override
    public void addOutgoingPath(Path p){
        super.addOutgoingPath(p);
        //p.getSource().addIncomingPath(p);
        p.getDestination().addIncomingPath(p);
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
    public double getDistance2() {
        return distance2;
    }

    @Override
    public void setDistance2(double distance) {
        this.distance2 = distance;
    }

    @Override
    public Node getPredecessor() {
        return predecessor;
    }

    @Override
    public Node getPredecessor2() {
        return predecessor2;
    }

    @Override
    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public void setPredecessor2(Node predecessor) {
        this.predecessor2 = predecessor;
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
    public boolean getVisited2() {
        return visited2;
    }

    @Override
    public void setVisited2(boolean b) {
        visited2 = b;
    }

    @Override
    public List<Path> getIncomingPaths() {
        return incomingPaths;
    }

    @Override
    public List<Double> getDistancesFromLandmarks() {
        return landmarkDistancesFromLandmark;
    }

    @Override
    public List<Double> getDistancesToLandmarks() {
        return landmarkDistancesToLandmark;
    }

    @Override
    public void addLandmarkDistanceTo( double distToLandmark) {
        landmarkDistancesToLandmark.add(distToLandmark);
    }

    @Override
    public void addLandmarkDistanceFrom(double distFromLandmark) {
        landmarkDistancesFromLandmark.add(distFromLandmark);
    }

    @Override
    public void addIncomingPath(Path path) {
        incomingPaths.add(path);
    }

    @Override
    public String toString() {
        return "NodeImpl{" +
                "id=" + id +
                '}';
    }
}
