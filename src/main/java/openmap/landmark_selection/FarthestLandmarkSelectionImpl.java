package openmap.landmark_selection;

import openmap.alternative_pathfinders.LandmarkDijkstraImpl;
import openmap.framework.Graph;
import openmap.framework.Node;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class FarthestLandmarkSelectionImpl extends LandmarkSelectionAbstract{

    public FarthestLandmarkSelectionImpl(Graph graph){
        super(graph);
    }

    @Override
    public void findLandmarks(int k) {
        Object[] values = graph.getNodeMap().values().toArray();
        landmarks.clear();
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().getLandmarkDistances().clear();
        }


        Random random = new Random();
        //add random landmark initially
        Node landmark = (Node)values[random.nextInt(values.length)];

        //find node farthest from the random node and add to landmarks
        pf.getShortestPath(landmark, landmark);
        double distance = 0;
        Node bestNode = null;
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            if(e.getValue().getDistance() > distance && e.getValue().getDistance() != Double.MAX_VALUE){
                distance = e.getValue().getDistance();
                bestNode = e.getValue();
            }
        }
        processLandmark(bestNode);
        landmarks.add(bestNode);

        //find node farthest from all known landmarks
        for(int i = 1; i < k; i++){
            distance = 0;
            double bestDistance = 0;
            bestNode = null;
            for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
                for(double d : e.getValue().getLandmarkDistances()){
                    if(d == Double.MAX_VALUE){ //don't select islands
                        distance = 0;
                        break;
                    }
                    distance = distance + d;
                }

                if(distance > bestDistance){
                    bestDistance = e.getValue().getDistance();
                    bestNode = e.getValue();
                }
            }
            processLandmark(bestNode);
            landmarks.add(bestNode);
        }

    }

    private void processLandmark(Node l){
        pf.getShortestPath(l, l);
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().addLandmarkDistance(e.getValue().getDistance());
        }
    }
}
