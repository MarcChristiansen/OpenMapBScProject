package openmap.framework;

import java.util.List;

public interface LandmarkSelection {

    public List<Node> findLandmarks(int k);

    public void preProcessNodes(List<Node> landmarks);
}
