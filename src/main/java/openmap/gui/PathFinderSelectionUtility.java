package openmap.gui;

import openmap.alternative_pathfinders.*;
import openmap.framework.Graph;
import openmap.framework.PathFinder;
import openmap.standard.DijkstraImpl;


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
        pathfinders = new PathFinder[9];
    }

    private final Graph graph;
    private final PathFinder[] pathfinders;

    private final String[] pathFinderStrings = { "Dijkstra", "Dijkstra Wrong", "A*", "Bidirectional Dijkstra", "Very wrong Bi Dijkstra", "Bidirectional A*", "Bidirectional A* Wrong", "Landmark", "Landmark bi dir"};

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
            if(pathfinders[0] == null){ pathfinders[0] = new DijkstraImpl(graph); }
            return pathfinders[0];
        }
        else if(pathFinderStrings[1].equals(finderId)){
            if(pathfinders[1] == null){ pathfinders[1] = new DijkstraWrongImpl(graph); }
            return pathfinders[1];
        }
        else if(pathFinderStrings[2].equals(finderId)){
            if(pathfinders[2] == null){ pathfinders[2] = new AStarImpl(graph); }
            return pathfinders[2];
        }
        if(pathFinderStrings[3].equals(finderId)){
            if(pathfinders[3] == null){ pathfinders[3] = new DijkstraBiDirImpl(graph); }
            return pathfinders[3];
        }
        if(pathFinderStrings[4].equals(finderId)){
            if(pathfinders[4] == null){ pathfinders[4] = new DijkstraBiDirVeryWrongImpl(graph); }
            return pathfinders[4];
        }

        if(pathFinderStrings[5].equals(finderId)){
            if(pathfinders[5] == null){ pathfinders[5] = new AStarImplBiDirImpl(graph); }
            return pathfinders[5];
        }

        if(pathFinderStrings[6].equals(finderId)){
            if(pathfinders[6] == null){ pathfinders[6] = new AStarImplBiDirImplWrong(graph); }
            return pathfinders[6];
        }

        if(pathFinderStrings[7].equals(finderId)){
            if(pathfinders[7] == null){ pathfinders[7] = new LandmarkPathfinderImpl(graph); }
            return pathfinders[7];
        }

        if(pathFinderStrings[8].equals(finderId)){
            if(pathfinders[8] == null){ pathfinders[8] = new LandmarkBiDirConsistentImpl(graph); }
            return pathfinders[8];
        }

        return null; //Todo Possibly make exception...
    }


}
