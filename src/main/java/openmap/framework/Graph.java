package openmap.framework;

import java.util.Map;

public interface Graph {

    public Map<Long, Node> getNodeMap();

    public Bounds getBounds();
}
