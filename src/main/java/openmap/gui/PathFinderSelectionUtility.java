package openmap.gui;

import openmap.alternative.AStarImpl;
import openmap.framework.Graph;
import openmap.framework.PathFinder;
import openmap.standard.DijkstraBiDirImpl;
import openmap.standard.DijkstraBiDirVeryWrongImpl;
import openmap.standard.DijkstraImpl;
import openmap.standard.DijkstraWrongImpl;

public class PathFinderSelectionUtility {

    public PathFinderSelectionUtility(Graph graph) {
        this.graph = graph;
    }

    private Graph graph;

    private String[] pathFinderStrings = { "Dijkstra", "Dijkstra Wrong", "A*", "Bidirectional Dijkstra", "Very wrong Bi Dijkstra"};

    //Pathfinders
    private DijkstraImpl dijkstraPathfinder;
    private DijkstraWrongImpl dijkstraWrongPathfinder;
    private AStarImpl AStarPathFinder;
    private DijkstraBiDirImpl DijkstraBiDirPathFinder;
    private DijkstraBiDirVeryWrongImpl DijkstraBiDirVeryWrongPathFinder;



    public String[] getPathFinderStrings() {
        return pathFinderStrings;
    }

    public PathFinder getPathFinder (String finderId) {

        if(pathFinderStrings[0].equals(finderId)){
            if(dijkstraPathfinder == null){ dijkstraPathfinder = new DijkstraImpl(graph); }
            return dijkstraPathfinder;
        }
        else if(pathFinderStrings[1].equals(finderId)){
            if(dijkstraWrongPathfinder == null){ dijkstraWrongPathfinder = new DijkstraWrongImpl(graph); }
            return dijkstraWrongPathfinder;
        }
        else if(pathFinderStrings[2].equals(finderId)){
            if(AStarPathFinder == null){ AStarPathFinder = new AStarImpl(graph); }
            return AStarPathFinder;
        }
        if(pathFinderStrings[3].equals(finderId)){
            if(DijkstraBiDirPathFinder == null){ DijkstraBiDirPathFinder = new DijkstraBiDirImpl(graph); }
            return DijkstraBiDirPathFinder;
        }
        if(pathFinderStrings[4].equals(finderId)){
            if(DijkstraBiDirVeryWrongPathFinder == null){ DijkstraBiDirVeryWrongPathFinder = new DijkstraBiDirVeryWrongImpl(graph); }
            return DijkstraBiDirVeryWrongPathFinder;
        }

        return null; //Todo Possibly make exception...
    }


}
