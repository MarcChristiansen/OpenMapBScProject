package openmap.standard;

import openmap.framework.Node;
import openmap.framework.Path;

import java.util.List;

public class NodeImpl implements Node {

    private long id;
    private double lat;
    private double lon;

    public NodeImpl(long id, double lat, double lon){
        this.id = id;
        this.lat = lat;
        this.lon = lon;
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
        return null;
    }

    @Override
    public void addPath(Path path) {

    }
}
