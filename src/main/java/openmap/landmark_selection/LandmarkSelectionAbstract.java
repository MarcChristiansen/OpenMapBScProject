package openmap.landmark_selection;

import openmap.alternative_pathfinders.LandmarkDijkstraImpl;
import openmap.framework.Graph;
import openmap.framework.LandmarkSelection;
import openmap.framework.Node;
import openmap.framework.PathFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class LandmarkSelectionAbstract implements LandmarkSelection {



    protected Graph graph;
    protected List<Node> landmarks;
    protected PathFinder pf;

    public LandmarkSelectionAbstract(Graph graph){
        this.graph = graph;
        this.landmarks = new ArrayList<Node>();
        this.pf = new LandmarkDijkstraImpl(graph);
    }

    @Override
    public List<Node> getLandmarks() {
        return landmarks;
    }



}
