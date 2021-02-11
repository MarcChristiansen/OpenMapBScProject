package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;
import openmap.utility.CoordinateUtility;
import org.locationtech.jts.geom.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeImpl implements Node, Serializable {

    private long id;

    Coordinate coordinate;
    private List<Path> pathList;

    public NodeImpl(long id, double lat, double lon){
        this.id = id;

        coordinate = new Coordinate(lat, lon);

        coordinate = CoordinateUtility.CoordinateConversion.latLonToUtm32N(coordinate);

        pathList = new ArrayList<>();

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

}
