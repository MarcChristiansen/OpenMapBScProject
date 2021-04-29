package openmap.framework;

import java.util.List;

/**
 * Interface of objects used to build graphs
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public interface LandmarkSelection {

    /**
     * Setup landmarks in nodes
     * @param k The amount of landmarks to create
     */
    public void findLandmarks(int k);

    /**
     * Get list of nodes that are landmarks
     * @return List of nodes that are landmarks
     */
    public List<Node> getLandmarksTo();

    /**
     * Get list of nodes that are landmarks
     * @return List of nodes that are landmarks
     */
    public List<Node> getLandmarksFrom();
}
