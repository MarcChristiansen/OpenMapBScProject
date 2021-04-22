package openmap.framework;

import java.util.List;

public interface LandmarkSelection {

    public void findLandmarks(int k);

    public List<Node> getLandmarks();
}
