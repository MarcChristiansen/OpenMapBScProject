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

public abstract class LandmarkSelectionAbstract implements LandmarkSelection {



    protected Graph graph;
    protected List<Node> landmarksTo;
    protected List<Node> landmarksFrom;
    protected PathFinder pfForward;
    protected PathFinder pfBackward;

    public LandmarkSelectionAbstract(Graph graph){
        this.graph = graph;
        this.landmarksTo = new ArrayList<Node>();
        this.landmarksFrom = new ArrayList<Node>();
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

}
