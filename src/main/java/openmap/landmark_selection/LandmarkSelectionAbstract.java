package openmap.landmark_selection;

import openmap.alternative_pathfinders.LandmarkDijkstraImplBackard;
import openmap.alternative_pathfinders.LandmarkDijkstraImplForward;
import openmap.framework.Graph;
import openmap.framework.LandmarkSelection;
import openmap.framework.Node;
import openmap.framework.PathFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base implementation for some useful things related to landmark selection
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public abstract class LandmarkSelectionAbstract implements LandmarkSelection {

    protected Graph graph;
    protected List<Node> landmarksTo;
    protected List<Node> landmarksFrom;
    protected PathFinder pfForward;
    protected PathFinder pfBackward;
    protected long executionTime;

    public LandmarkSelectionAbstract(Graph graph){
        this.graph = graph;
        this.landmarksTo = new ArrayList<>();
        this.landmarksFrom = new ArrayList<>();
        this.pfForward = new LandmarkDijkstraImplForward(graph);
        this.pfBackward = new LandmarkDijkstraImplBackard(graph);
    }

    @Override
    public List<Node> getLandmarksTo() {
        return landmarksTo;
    }

    @Override
    public List<Node> getLandmarksFrom() { return landmarksFrom; }

    protected void clearAndCreateNewArrays(int k){
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().createNewLandmarkArrays(k);
        }
    }

    @Override
    public long getExecutionTime() {
        return executionTime;
    }

}
