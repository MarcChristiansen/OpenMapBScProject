package openmap.gui;

import openmap.alternative.AStarImpl;
import openmap.alternative.AStarImplBiDirImplWrong;
import openmap.alternative.AStarImplBiDirImpl;
import openmap.framework.Graph;
import openmap.framework.PathFinder;
import openmap.standard.DijkstraBiDirImpl;
import openmap.standard.DijkstraBiDirVeryWrongImpl;
import openmap.standard.DijkstraImpl;
import openmap.standard.DijkstraWrongImpl;


/**
 * Simple utility that only uses one instance of each pathfinder for the GUI.
 * Also only creates an instance when a pothfinder is selected
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 013-04-2021
 */
public class PathFinderSelectionUtility {

    /**
     * Create a selection utility with a given graph
     * All pathfinders will be linked to this graph
     * @param graph The graph to use
     */
    public PathFinderSelectionUtility(Graph graph) {
        this.graph = graph;
    }

    private final Graph graph;

    private final String[] pathFinderStrings = { "Dijkstra", "Dijkstra Wrong", "A*", "Bidirectional Dijkstra", "Very wrong Bi Dijkstra", "Bidirectional A*", "Bidirectional A* Wrong"};

    //Pathfinders
    private DijkstraImpl dijkstraPathfinder;
    private DijkstraWrongImpl dijkstraWrongPathfinder;
    private AStarImpl AStarPathFinder;
    private DijkstraBiDirImpl DijkstraBiDirPathFinder;
    private DijkstraBiDirVeryWrongImpl DijkstraBiDirVeryWrongPathFinder;
    private AStarImplBiDirImpl AStarImplBiDirPathFinder;
    private AStarImplBiDirImplWrong AStarImplBiDirPathFinderWrong;


    /**
     * Get pathfinder string names.
     * Primarily for use with something like a combobox
     * @return A string array of id's usable in getPathFinder()
     */
    public String[] getPathFinderStrings() {
        return pathFinderStrings;
    }

    /**
     * Given a string from the getPathFinderStrings() array return the relevant pathfinder
     * @param finderId The string id
     * @return Any relevant pathfinder, or null if none is found
     */
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

        if(pathFinderStrings[5].equals(finderId)){
            if(AStarImplBiDirPathFinder == null){ AStarImplBiDirPathFinder = new AStarImplBiDirImpl(graph); }
            return AStarImplBiDirPathFinder;
        }

        if(pathFinderStrings[6].equals(finderId)){
            if(AStarImplBiDirPathFinderWrong == null){ AStarImplBiDirPathFinderWrong = new AStarImplBiDirImplWrong(graph); }
            return AStarImplBiDirPathFinderWrong;
        }

        return null; //Todo Possibly make exception...
    }


}
