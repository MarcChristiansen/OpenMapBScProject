package openmap.standard;

import openmap.parsing.json.JsonGraphConstants;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.utility.CoordinateUtility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeImpl implements Node, Serializable, Comparable<Node> {

    private long id;
    private Long predecessorId;
    private double distance;
    private boolean visited;

    Coordinate coordinate;
    private List<Path> pathList;

    /**
     * Create a new node from an id and latitude and longitude (This uses UTM32N
     * @param id The id of the node
     * @param lat The latitude of the node
     * @param lon The longitude of the node
     */
    public NodeImpl(long id, double lat, double lon){
        this.id = id;

        coordinate = new Coordinate(lat, lon);

        coordinate = CoordinateUtility.CoordinateConversion.latLonToUtm32N(coordinate);

        pathList = new ArrayList<>();
    }

    public NodeImpl(long id, double x, double y, List<Path> pathList){
        this.id = id;

        coordinate = new Coordinate(x, y);

        this.pathList = pathList;
    }

    public NodeImpl(JSONObject obj){
        this.id = (Long)obj.get(JsonGraphConstants.NodeId);
        this.coordinate = new Coordinate((Double)obj.get(JsonGraphConstants.NodeX), (Double)obj.get(JsonGraphConstants.NodeY));
        this.pathList = new ArrayList<>();
        this.distance = Double.MAX_VALUE;
        this.predecessorId = null;

        JSONArray pArray = (JSONArray)obj.get(JsonGraphConstants.NodePath);

        this.pathList = new ArrayList<>();

        for (Object pathObj : pArray) {
            pathList.add(new StandardPathImpl((JSONObject) pathObj));
        }
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public double getLat() { return CoordinateUtility.CoordinateConversion.utm32NToLatLon(coordinate).x; }

    @Override
    public double getLon() { return CoordinateUtility.CoordinateConversion.utm32NToLatLon(coordinate).y; }

    @Override
    public double getX() { return coordinate.x; }

    @Override
    public double getY() { return coordinate.y; }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public Long getPredecessor() {
        return predecessorId;
    }

    @Override
    public void setPredecessor(Long predecessorId) {
        this.predecessorId = predecessorId;
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
    public List<Path> getPaths() {
        return pathList; //Todo maybe make read-only?
    }

    @Override
    public void addPath(Path path) {
        pathList.add(path);
    }

    @Override
    public void convertPathForSerialization(){
        pathList.forEach(path -> {
            path.prepareForSerialization();
        });
    }

    @Override
    public void convertPathDeserialization(Map<Long, Node> nodeMap){
        pathList.forEach(path -> {
            path.doDeserialization(nodeMap);
        });
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(JsonGraphConstants.NodeId, id);
        obj.put(JsonGraphConstants.NodeX, coordinate.x);
        obj.put(JsonGraphConstants.NodeY, coordinate.y);

        JSONArray jArray = new JSONArray();
        for (Path p : pathList) {
            jArray.add(p.getJSONObject());
        }

        obj.put(JsonGraphConstants.NodePath, jArray);

        return obj;
    }

    @Override
    public int compareTo(Node o) {
        if(this.getDistance() < o.getDistance()){
            return -1;
        }
        if(this.getDistance() > o.getDistance()){
            return 1;
        }
        return 0;
    }

}
