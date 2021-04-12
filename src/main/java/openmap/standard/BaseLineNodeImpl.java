package openmap.standard;

import com.fasterxml.jackson.core.JsonGenerator;
import openmap.parsing.json.JsonGraphConstants;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.special.DecodingPathImpl;
import openmap.utility.CoordinateUtility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract public class BaseLineNodeImpl implements Node, Serializable, Comparable<Node> {

    protected long id;
    protected double x, y;
    protected List<Path> pathList;

    /**
     * Create a new node from an id and latitude and longitude (This uses UTM32N
     * @param id The id of the node
     * @param lat The latitude of the node
     * @param lon The longitude of the node
     */
    public BaseLineNodeImpl(long id, double lat, double lon){
        this.id = id;

        Coordinate coordinate = new Coordinate(lat, lon);

        coordinate = CoordinateUtility.CoordinateConversion.latLonToUtm32N(coordinate);

        this.x = coordinate.x;
        this.y = coordinate.y;

        pathList = new ArrayList<>();
    }

    public BaseLineNodeImpl(long id, double x, double y, List<Path> pathList){
        this.id = id;

        this.x = x;
        this.y = y;

        this.pathList = pathList;
    }

    public BaseLineNodeImpl(JSONObject obj){
        this.id = (Long)obj.get(JsonGraphConstants.NodeId);
        this.x = (Double)obj.get(JsonGraphConstants.NodeX);
        this.y = (Double)obj.get(JsonGraphConstants.NodeY);

        this.pathList = new ArrayList<>();

        JSONArray pArray = (JSONArray)obj.get(JsonGraphConstants.NodePath);

        this.pathList = new ArrayList<>();

        for (Object pathObj : pArray) {
            pathList.add(new DecodingPathImpl((JSONObject) pathObj));
        }
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public double getLat() { return CoordinateUtility.CoordinateConversion.utm32NToLatLon(new Coordinate(x, y)).x; }

    @Override
    public double getLon() { return CoordinateUtility.CoordinateConversion.utm32NToLatLon(new Coordinate(x, y)).y; }

    @Override
    public double getX() { return x; }

    @Override
    public double getY() { return y; }

    @Override
    public List<Path> getOutgoingPaths() {
        return pathList; //Todo maybe make read-only?
    }

    @Override
    public void addOutgoingPath(Path path) {
        pathList.add(path);
    }

    @Override
    /**
     * Deserialize paths MUST ONLY BE RUN ONCE
     */
    public void convertPathDeserialization(Map<Long, Node> nodeMap){
        //We map the current list of paths to standard paths.
        //Intended for the path in the list to be decoding paths but to avoid crashes it works for all types as it only uses interface functions.
        pathList = pathList.stream()
                            .map(path -> new StandardPathImpl(nodeMap.get(path.getDestinationId()), this, path.getWeight()))
                            .collect(Collectors.toCollection(ArrayList::new));

        //pathList.forEach(p -> p.getSource().addIncomingPath(p));
        pathList.forEach(p -> p.getDestination().addIncomingPath(p));
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(JsonGraphConstants.NodeId, id);
        obj.put(JsonGraphConstants.NodeX, x);
        obj.put(JsonGraphConstants.NodeY, y);

        JSONArray jArray = new JSONArray();
        for (Path p : pathList) {
            jArray.add(p.getJSONObject());
        }

        obj.put(JsonGraphConstants.NodePath, jArray);


        return obj;
    }

    @Override
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException {
        jGenerator.writeStartObject();

        jGenerator.writeArrayFieldStart(JsonGraphConstants.NodePath);
        for (Path p : pathList) {
            p.WriteToJsonGenerator(jGenerator);
        }
        jGenerator.writeEndArray();

        jGenerator.writeNumberField(JsonGraphConstants.NodeId, id);
        jGenerator.writeNumberField(JsonGraphConstants.NodeX, x);
        jGenerator.writeNumberField(JsonGraphConstants.NodeY, y);

        jGenerator.writeEndObject();
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
