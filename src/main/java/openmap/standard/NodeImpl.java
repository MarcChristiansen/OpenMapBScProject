package openmap.standard;

import com.fasterxml.jackson.core.JsonGenerator;
import openmap.parsing.json.JsonGraphConstants;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.utility.CoordinateUtility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeImpl extends BaseLineNodeImpl {

    private Long predecessorId;
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
        this.predecessorId = null;
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
}
