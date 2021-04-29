package openmap.landmark_selection;

import openmap.framework.Graph;
import openmap.framework.Node;

import java.util.Random;

public class RandomizedLandmarkSelectionImpl extends LandmarkSelectionAbstract {


    public RandomizedLandmarkSelectionImpl(Graph graph){
        super(graph);
    }

    @Override
    public void findLandmarks(int k) {
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
    }

    private void preProcessNodes() {

        this.clearPreviousLandmarksFromNodes();

        Object[] values = graph.getNodeMap().values().toArray();
        for(Node L : landmarksTo){
            //run landmark Dijkstra
            pfForward.getShortestPath(L, L);
            pfBackward.getShortestPath(L,L);
            for(Object n : values){
                ((Node)n).addLandmarkDistanceTo(((Node)n).getDistance2());
                ((Node)n).addLandmarkDistanceFrom(((Node)n).getDistance());
               //System.out.println(((Node)n).getDistance2());
            }
        }
    }
}
