package openmap.landmark_selection;

import openmap.framework.Graph;
import openmap.framework.Node;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FarthestLandmarkSelectionImpl extends LandmarkSelectionAbstract{

    public FarthestLandmarkSelectionImpl(Graph graph){
        super(graph);
    }

    @Override
    public void findLandmarks(int k) {
        Object[] values = graph.getNodeMap().values().toArray();
        landmarksTo.clear();
        landmarksFrom.clear();
        this.clearPreviousLandmarksFromNodes();


        Random random = new Random();
        random.setSeed(1231231231); //Consistency //TODO possibly move this to somewhere else
        //add random landmark initially
        Node landmark = (Node)values[random.nextInt(values.length)];

        //find node farthest from the random node and add to landmarks
        pfForward.getShortestPath(landmark, landmark);
        pfBackward.getShortestPath(landmark, landmark);
        double distanceFrom = 0;
        double distanceTo = 0;
        Node bestNodeFrom = null;
        Node bestNodeTo = null;
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            if(e.getValue().getDistance() > distanceTo && e.getValue().getDistance() != Double.MAX_VALUE){
                distanceTo = e.getValue().getDistance();
                bestNodeTo = e.getValue();
            }

            if(e.getValue().getDistance2() > distanceFrom && e.getValue().getDistance2() != Double.MAX_VALUE){
                distanceFrom = e.getValue().getDistance2();
                bestNodeFrom = e.getValue();
            }
        }
        processLandmarkTo(bestNodeTo);
        processLandmarkFrom(bestNodeFrom);
        landmarksFrom.add(bestNodeFrom);
        landmarksTo.add(bestNodeTo);

        System.out.println("Landmark number " + 1 + " Processed");

        //find node farthest from all known landmarks
        for(int i = 1; i < k; i++){
            System.out.println("Landmark number " + (i+1) + " Processed");
            double bestDistanceFrom= 0;
            double bestDistanceTo= 0;
            bestNodeFrom = null;
            bestNodeTo = null;
            for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
                distanceFrom = minFromDoubleList(e.getValue().getDistancesFromLandmarks());

                if(distanceFrom == Double.MAX_VALUE){ //don't select islands
                    distanceFrom = 0;
                }

                if(distanceFrom > bestDistanceFrom){
                    bestDistanceFrom = distanceFrom;
                    bestNodeFrom = e.getValue();
                }


                distanceTo = minFromDoubleList(e.getValue().getDistancesToLandmarks());
                if(distanceTo == Double.MAX_VALUE){ //don't select islands
                    distanceTo = 0;
                }

                if(distanceTo > bestDistanceTo){
                    bestDistanceTo = distanceTo;
                    bestNodeTo = e.getValue();
                }
            }
            processLandmarkFrom(bestNodeFrom);
            landmarksFrom.add(bestNodeFrom);

            processLandmarkTo(bestNodeTo);
            landmarksTo.add(bestNodeTo);
        }

    }

    private void processLandmarkFrom(Node l){
        pfForward.getShortestPath(l, l);
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().addLandmarkDistanceFrom(e.getValue().getDistance());
        }
    }

    private void processLandmarkTo(Node l){
        pfBackward.getShortestPath(l, l);
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().addLandmarkDistanceTo(e.getValue().getDistance2());
        }
    }

    private double minFromDoubleList(List<Double> l) {
        double res = Double.MAX_VALUE;
        for(Double d : l){
            if(res > d){
                res = d;
            }
        }
        return res;
    }

}
