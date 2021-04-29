package openmap.landmark_selection;

import openmap.framework.Graph;
import openmap.framework.Node;

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
        this.clearAndCreateNewArrays(k);


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
        processLandmarkTo(bestNodeTo, 0);
        processLandmarkFrom(bestNodeFrom, 0);
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
                distanceFrom = minFromDoubleListKFirstEntries(e.getValue().getDistancesFromLandmarks(), i-1);

                if(distanceFrom == Double.MAX_VALUE){ //don't select islands
                    distanceFrom = 0;
                }

                if(distanceFrom > bestDistanceFrom){
                    bestDistanceFrom = distanceFrom;
                    bestNodeFrom = e.getValue();
                }


                distanceTo = minFromDoubleListKFirstEntries(e.getValue().getDistancesToLandmarks(), i-1);
                if(distanceTo == Double.MAX_VALUE){ //don't select islands
                    distanceTo = 0;
                }

                if(distanceTo > bestDistanceTo){
                    bestDistanceTo = distanceTo;
                    bestNodeTo = e.getValue();
                }
            }
            processLandmarkFrom(bestNodeFrom, i);
            landmarksFrom.add(bestNodeFrom);

            processLandmarkTo(bestNodeTo, i);
            landmarksTo.add(bestNodeTo);
        }

    }

    private void processLandmarkFrom(Node l, int i){
        pfForward.getShortestPath(l, l);
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().setLandmarkDistanceFrom(e.getValue().getDistance(), i);
        }
    }

    private void processLandmarkTo(Node l, int i){
        pfBackward.getShortestPath(l, l);
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().setLandmarkDistanceTo(e.getValue().getDistance2(), i);
        }
    }

    private double minFromDoubleListKFirstEntries(double[] arr, int k) {
        double res = Double.MAX_VALUE;

        if(k > arr.length) { k = arr.length;}

        for(int i = 0; i <= k; i++){
            Double d = arr[i];

            if(res > d){
                res = d;
            }
        }
        return res;
    }

}
