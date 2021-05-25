package openmap.landmark_selection;

import openmap.framework.Graph;
import openmap.framework.Node;

import java.util.Random;

/**
 * Base implementation for a randomized landmark selection
 * Note: forward and backward landmarks are the same in this selection
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class RandomizedLandmarkSelectionImpl extends LandmarkSelectionAbstract {


    public RandomizedLandmarkSelectionImpl(Graph graph){
        super(graph);
    }

    @Override
    public void findLandmarks(int k) {

        long start = System.currentTimeMillis();

        Object[] values = graph.getNodeMap().values().toArray();
        landmarksTo.clear();
        landmarksFrom.clear();

        Random random = new Random();
        for(int i = 0; i < k; i++){
            Node landmark = (Node)values[random.nextInt(values.length)];
            landmarksTo.add(landmark);
            landmarksFrom.add(landmark);
        }

        preProcessNodes();

        executionTime = System.currentTimeMillis() - start;
    }

    private void preProcessNodes() {

        this.clearAndCreateNewArrays(landmarksTo.size());

        Object[] values = graph.getNodeMap().values().toArray();
        for(int i = 0; i < landmarksTo.size(); i++){
            Node L = landmarksTo.get(i);
            //run landmark Dijkstra
            pfForward.getShortestPath(L, L);
            pfBackward.getShortestPath(L,L);
            for(Object n : values){
                ((Node)n).setLandmarkDistanceTo(((Node)n).getDistance2(), i);
                ((Node)n).setLandmarkDistanceFrom(((Node)n).getDistance(), i);
               //System.out.println(((Node)n).getDistance2());
            }
        }
    }
}
