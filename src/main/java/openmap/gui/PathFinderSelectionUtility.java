package openmap.gui;

import openmap.alternative.AStarImpl;
import openmap.framework.Graph;
import openmap.framework.PathFinder;
import openmap.standard.DijkstraImpl;
import openmap.standard.DijkstraWrongImpl;

import javax.lang.model.element.UnknownElementException;

public class PathFinderSelectionUtility {

    public PathFinderSelectionUtility(Graph graph) {
        this.graph = graph;
    }

    private Graph graph;

    private String[] pathFinderStrings = { "Dijkstra", "Dijkstra Wrong", "A*"};

    //Pathfinders
    private DijkstraImpl dijkstraPathfinder;
    private DijkstraWrongImpl dijkstraWrongPathfinder;
    private AStarImpl AStarPathFinder;



    public String[] getPathFinderStrings() {
        return pathFinderStrings;
    }

    public PathFinder getPathFinder (String finderId) {

        if(pathFinderStrings[0].equals(finderId)){
            if(dijkstraPathfinder == null){ dijkstraPathfinder = new DijkstraImpl(graph); }
            return dijkstraPathfinder;
        }
        if(pathFinderStrings[1].equals(finderId)){
            if(dijkstraWrongPathfinder == null){ dijkstraWrongPathfinder = new DijkstraWrongImpl(graph); }
            return dijkstraWrongPathfinder;
        }
        if(pathFinderStrings[2].equals(finderId)){
            if(AStarPathFinder == null){ AStarPathFinder = new AStarImpl(graph); }
            return AStarPathFinder;
        }

        return null; //Todo Possibly make exception...
    }


}
