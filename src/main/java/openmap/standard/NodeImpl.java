package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeImpl implements Node, Serializable {

    private long id;
    private double lat;
    private double lon;
    private List<Path> pathList;

    public NodeImpl(long id, double lat, double lon){
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        pathList = new ArrayList<>();

    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLon() {
        return lon;
    }

    @Override
    public List<Path> getPaths() {
        return pathList; //Todo maybe make read-only?
    }

    @Override
    public void addPath(Path path) {
        pathList.add(path);
    }

    public void convertPathForSerialization(){
        pathList.forEach(path -> {
            path.prepareForSerialization();
        });
    }

    public void convertPathDeserialization(Map<Long, Node> nodeMap){
        pathList.forEach(path -> {
            path.doDeserialization(nodeMap);
        });
    }

}
