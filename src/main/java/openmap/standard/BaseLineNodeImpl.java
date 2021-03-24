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
    Coordinate coordinate;
    protected List<Path> pathList;

    /**
     * Create a new node from an id and latitude and longitude (This uses UTM32N
     * @param id The id of the node
     * @param lat The latitude of the node
     * @param lon The longitude of the node
     */
    public BaseLineNodeImpl(long id, double lat, double lon){
        this.id = id;

        coordinate = new Coordinate(lat, lon);

        coordinate = CoordinateUtility.CoordinateConversion.latLonToUtm32N(coordinate);

        pathList = new ArrayList<>();
    }

    public BaseLineNodeImpl(long id, double x, double y, List<Path> pathList){
        this.id = id;

        coordinate = new Coordinate(x, y);

        this.pathList = pathList;
    }

    public BaseLineNodeImpl(JSONObject obj){
        this.id = (Long)obj.get(JsonGraphConstants.NodeId);
        this.coordinate = new Coordinate((Double)obj.get(JsonGraphConstants.NodeX), (Double)obj.get(JsonGraphConstants.NodeY));
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
    public double getLat() { return CoordinateUtility.CoordinateConversion.utm32NToLatLon(coordinate).x; }

    @Override
    public double getLon() { return CoordinateUtility.CoordinateConversion.utm32NToLatLon(coordinate).y; }

    @Override
    public double getX() { return coordinate.x; }

    @Override
    public double getY() { return coordinate.y; }

    @Override
    public List<Path> getPaths() {
        return pathList; //Todo maybe make read-only?
    }

    @Override
    public void addPath(Path path) {
        pathList.add(path);
    }

    @Override
    public void convertPathDeserialization(Map<Long, Node> nodeMap){
        //We map the current list of paths to standard paths.
        //Intended for the path in the list to be decoding paths but to avoid crashes it works for all types as it only uses interface functions.
        pathList = pathList.stream()
                            .map(path -> new StandardPathImpl(nodeMap.get(path.getDestinationId()), path.getWeight()))
                            .collect(Collectors.toCollection(ArrayList::new));
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
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException {
        jGenerator.writeStartObject();

        jGenerator.writeArrayFieldStart(JsonGraphConstants.NodePath);
        for (Path p : pathList) {
            p.WriteToJsonGenerator(jGenerator);
        }
        jGenerator.writeEndArray();

        jGenerator.writeNumberField(JsonGraphConstants.NodeId, id);
        jGenerator.writeNumberField(JsonGraphConstants.NodeX, coordinate.x);
        jGenerator.writeNumberField(JsonGraphConstants.NodeY, coordinate.y);

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
