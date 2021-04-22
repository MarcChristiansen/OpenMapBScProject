package openmap.landmark_selection;

import openmap.alternative_pathfinders.LandmarkDijkstraImpl;
import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.landmark_selection.LandmarkSelectionAbstract;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class RandomizedLandmarkSelectionImpl extends LandmarkSelectionAbstract {


    public RandomizedLandmarkSelectionImpl(Graph graph){
        super(graph);
    }

    @Override
    public void findLandmarks(int k) {
        Object[] values = graph.getNodeMap().values().toArray();
        landmarks.clear();
        Random random = new Random();
        for(int i = 0; i < k; i++){
            Node landmark = (Node)values[random.nextInt(values.length)];
            landmarks.add(landmark);
        }

        preProcessNodes();
    }

    private void preProcessNodes() {

        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().getLandmarkDistances().clear();
        }

        Object[] values = graph.getNodeMap().values().toArray();
        for(Node L : landmarks){
            //run landmark Dijkstra
            pf.getShortestPath(L, L);
            for(Object n : values){
                ((Node)n).addLandmarkDistance(((Node)n).getDistance());
            }
        }
    }
}
