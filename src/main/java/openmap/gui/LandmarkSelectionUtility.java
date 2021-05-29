package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.LandmarkSelection;
import openmap.landmark_selection.FarthestLandmarkSelectionImpl;
import openmap.landmark_selection.FarthestLandmarkSelectionImplSame;
import openmap.landmark_selection.RandomizedLandmarkSelectionImpl;

import java.util.ArrayList;


/**
 * Simple utility that only uses one instance of each pathfinder for the GUI.
 * Also only creates an instance when a pothfinder is selected
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 013-04-2021
 */
public class LandmarkSelectionUtility {



    private final Graph graph;

    private final String[] LandmarkSelectionStrings = { "Farthest same", "Randomized", "Farthest"};

    LandmarkSelection[] landmarkSelections;

    /**
     * Create a selection utility with a given graph
     * All pathfinders will be linked to this graph
     * @param graph The graph to use
     */
    public LandmarkSelectionUtility(Graph graph) {
        this.graph = graph;
        landmarkSelections = new LandmarkSelection[3];
    }

    /**
     * Get landmark selector string names.
     * Primarily for use with something like a combobox
     * @return A string array of id's usable in getLandmarkSelector()
     */
    public String[] getLandmarkSelectionStrings() {
        return LandmarkSelectionStrings;
    }

    /**
     * Given a string from the getLandmarkSelectionStrings() array return the relevant Landmark selection algorithm
     * @param finderId The string id
     * @return Any relevant landmark selection algorithm, or null if none is found
     */
    public LandmarkSelection getLandmarkSelector (String finderId) {

        if(LandmarkSelectionStrings[0].equals(finderId)){
            if(landmarkSelections[0] == null){ landmarkSelections[0] = new FarthestLandmarkSelectionImplSame(graph); }
            return landmarkSelections[0];
        }

        if(LandmarkSelectionStrings[1].equals(finderId)){
            if(landmarkSelections[1] == null){ landmarkSelections[1] = new RandomizedLandmarkSelectionImpl(graph); }
            return landmarkSelections[1];
        }

        if(LandmarkSelectionStrings[2].equals(finderId)){
            if(landmarkSelections[2] == null){ landmarkSelections[2] = new FarthestLandmarkSelectionImpl(graph); }
            return landmarkSelections[2];
        }

        return null; //Todo Possibly make exception...
    }
}
