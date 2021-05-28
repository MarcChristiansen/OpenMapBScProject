package openmap.landmark_selection;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Implementation that tries to select the farthest landmark from all other landmarks
 * The primary focus is on maximizing the min distance for any new possible landmark.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class FarthestLandmarkSelectionImplSame extends LandmarkSelectionAbstract{

    public FarthestLandmarkSelectionImplSame(Graph graph){
        super(graph);
    }

    @Override
    public void findLandmarks(int k) {

        long start = System.currentTimeMillis();

        Object[] values = graph.getNodeMap().values().toArray();
        landmarksTo.clear();
        landmarksFrom.clear();
        this.clearAndCreateNewArrays(k);


        Random random = new Random();
        random.setSeed(1231231231); //Consistency //TODO possibly move this to somewhere else
        //add random verificationNode initially
        Node verificationNode = (Node)values[random.nextInt(values.length)];

        //find node farthest from the random node and add to landmarks



        processLandmarkFrom(verificationNode, k-1);
        processLandmarkTo(verificationNode, k-1);

        double distanceTo = 0;
        Node bestNodeTo = null;
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            if(e.getValue().getDistance() > distanceTo && e.getValue().getDistance() != Double.MAX_VALUE){
                distanceTo = e.getValue().getDistance();
                bestNodeTo = e.getValue();
            }
        }
        processLandmarkTo(bestNodeTo, 0);
        processLandmarkFrom(bestNodeTo, 0);
        landmarksFrom.add(bestNodeTo);
        landmarksTo.add(bestNodeTo);

        //System.out.println("Landmark number " + 1 + " Processed");

        ArrayList<Node> usableNodes = new ArrayList<>();
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            if(!shouldBanNode(e.getValue())){
                usableNodes.add(e.getValue());
            }
        }
        usableNodes.trimToSize(); //Might not be needed, but won't hurt.

        //find node farthest from all known landmarks
        for(int i = 1; i < k; i++){
            //System.out.println("Landmark number " + (i+1) + " Processed");
            double bestDistanceFrom= 0;
            double distanceFrom = 0;
            double bestDistanceTo= 0;
            bestNodeTo = null;
            for(Node n : usableNodes){

                distanceFrom = minFromDoubleListKFirstEntries(n.getDistancesFromLandmarks(), i);
                distanceTo = minFromDoubleListKFirstEntries(n.getDistancesToLandmarks(), i);

                if(distanceTo == Double.MAX_VALUE || distanceFrom == Double.MAX_VALUE || n.getDistancesFromLandmarks()[k-1] == Double.MAX_VALUE || n.getDistancesToLandmarks()[k-1] == Double.MAX_VALUE){ //don't select islands
                    distanceTo = 0;
                }

                if(distanceTo > bestDistanceTo){
                    bestDistanceTo = distanceTo;
                    bestNodeTo = n;
                }
            }
            System.out.println(i);

            processLandmarkFrom(bestNodeTo, i);
            landmarksFrom.add(bestNodeTo);
            processLandmarkTo(bestNodeTo, i);
            landmarksTo.add(bestNodeTo);

            System.out.println(((Node)(values[random.nextInt(values.length)])).getDistancesFromLandmarks()[i]);
            System.out.println(((Node)(values[random.nextInt(values.length)])).getDistancesToLandmarks()[i]);
        }

        executionTime = System.currentTimeMillis() - start;

    }

    private boolean shouldBanNode(Node e) {
        boolean isFine = false;

        for (Path p : e.getOutgoingPaths()) {
            isFine = false;
            for(Path pe : p.getDestination().getOutgoingPaths()){
                if(pe.getDestination() == e){
                    isFine = true;
                    break;
                }
            }
            if(!isFine)
                return true;

        }

        return false;
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

        for(int i = 0; i < k; i++){
            Double d = arr[i];

            if(res > d){
                res = d;
            }
        }
        return res;
    }

}
