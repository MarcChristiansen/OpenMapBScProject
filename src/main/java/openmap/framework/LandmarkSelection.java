package openmap.framework;

import java.util.List;

public interface LandmarkSelection {

    public List<Node> findLandmarks(Graph graph, int k);

    public void preProcessNodes(Graph graph, List<Node> landmarks);
}
