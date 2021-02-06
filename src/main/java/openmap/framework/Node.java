package openmap.framework;

import java.util.List;

public interface Node {
    public long getId();
    public double getLat();
    public double getLon();
    public List<Path> getPaths();

}
