package openmap.gui;

import openmap.framework.Graph;
import openmap.framework.LandmarkSelection;
import openmap.landmark_selection.RandomizedLandmarkSelectionImpl;


/**
 * Simple utility that only uses one instance of each pathfinder for the GUI.
 * Also only creates an instance when a pothfinder is selected
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 013-04-2021
 */
public class LandmarkSelectionUtility {

    /**
     * Create a selection utility with a given graph
     * All pathfinders will be linked to this graph
     * @param graph The graph to use
     */
    public LandmarkSelectionUtility(Graph graph) {
        this.graph = graph;
    }

    private final Graph graph;

    private final String[] LandmarkSelectionStrings = { "Randomized"};

    //Landmark Selectors
    private RandomizedLandmarkSelectionImpl randomizedLandmarkSelection;


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
            if(randomizedLandmarkSelection == null){ randomizedLandmarkSelection = new RandomizedLandmarkSelectionImpl(graph); }
            return randomizedLandmarkSelection;
        }

        return null; //Todo Possibly make exception...
    }


}
