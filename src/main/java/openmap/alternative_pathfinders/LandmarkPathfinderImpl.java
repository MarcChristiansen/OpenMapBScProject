package openmap.alternative_pathfinders;

import openmap.framework.*;
import openmap.gui.NodeDrawingInfo;
import openmap.landmark_selection.FarthestLandmarkSelectionImpl;
import openmap.standard.AbstractPathfinder;
import openmap.standard.NodeWrapperImpl;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Landmark based pathfinding. Works in a similar way to A* but with a modified heuristic.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class LandmarkPathfinderImpl extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> priorityQueue;

    private Node currTarget;
    private int landmarkSubsetSize = 2;
    private int defaultLandmarkAmount = 20;
    private List<Integer> landmarks;

    private boolean preProcessDone;

    public LandmarkPathfinderImpl(Graph graph){
        this(graph, 20);
    }

    public LandmarkPathfinderImpl(Graph graph, int defaultLandmarkAmount){
        super(graph);
        priorityQueue = new PriorityQueue<NodeWrapper>();
        landmarks = new ArrayList<Integer>();
        this.defaultLandmarkAmount = defaultLandmarkAmount;
    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {

        landmarks.clear();

        if(source.getDistancesToLandmarks().length == 0) {
            System.out.println("Attempt to get path from landmarks without landmarks, using the default setting of farthest landmarks with k = 20");
            FarthestLandmarkSelectionImpl fls = new FarthestLandmarkSelectionImpl(graph);
            fls.findLandmarks(this.defaultLandmarkAmount);
        }

        //Prepare for A* run
        clearDistanceAndPredecessor();
        priorityQueue.clear();

        //Initial setup
        long start = System.currentTimeMillis();
        nodesVisited = 0;
        nodesScanned = 0;
        List<Node> path = null;
        currTarget = destination;

        //pick best landmark to work from
        FindLandmarkSubset(source);
        //Quick sanity check to ensure path actually exists by checking if they can both reach a chosen landmark.
        for(Integer i : landmarks) {
            if(source.getDistancesToLandmarks()[i] == Double.MAX_VALUE && destination.getDistancesToLandmarks()[i] < Double.MAX_VALUE){
                setExecutionTimeFromStart(start);
                return null; //No path exists.
            }
        }

        //find largest h
        source.setDistance(0);
        double h = getLowerbound(source);
        priorityQueue.add(new NodeWrapperImpl(source, h));

        NodeWrapper currNodeW = null;
        while (!priorityQueue.isEmpty()){
            currNodeW = priorityQueue.poll();


            if(currNodeW.getNode() == currTarget){
                path = retraceSteps(source);
                break;
            }

            if(!currNodeW.getNode().getVisited()){
                nodesVisited++;
                currNodeW.getNode().setVisited(true);
                for (Path p: currNodeW.getNode().getOutgoingPaths()) {
                    double totalWeight = currNodeW.getNode().getDistance()+p.getWeight();
                    Node pathDest = p.getDestination();

                    //If the newly discovered node has not been handled before or we found a shorter path to it
                    if(totalWeight < pathDest.getDistance()) {
                        pathDest.setDistance(totalWeight);
                        pathDest.setPredecessor(currNodeW.getNode());
                        h = getLowerbound(pathDest);

                        nodesScanned++;
                        priorityQueue.add(new NodeWrapperImpl(pathDest, totalWeight+h));
                    }
                }
            }
        }

        setExecutionTimeFromStart(start);
        //System.out.println("Landmark took " + (this.executionTime) + " ms");

        return path;
    }

    private void setExecutionTimeFromStart(long start) {
        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
    }

    private double getLowerbound(Node currNode) {
        double h = 0;
        for(int i : landmarks){
            if(h < h(currNode, i)){
                h = h(currNode, i);
            }
        }
        return h;
    }

    private void FindLandmarkSubset(Node source) {
        //really slow, but easy to read
        //also is only run once when getting shortest path
        //amount of landmarks and subsetsize are also low

        int landmark;
        int bestLandmark;
        double h = 0;
        for(int j = 0; j < landmarkSubsetSize; j++){
            bestLandmark = 0;
            h = 0;
            for(int i = 0; i < source.getDistancesFromLandmarks().length; i++){
                if(!landmarks.contains(i)){
                    landmark = i;
                    if(h < h(source, landmark)){
                        bestLandmark = landmark;
                        h = h(source, landmark);
                    }
                }
            }
            landmarks.add(bestLandmark);
        }
    }

    @Override
    public Function<Node, NodeDrawingInfo> getVisitedCheckFunction() {
        return ((Node n) -> {
            if(n.getDistance() < Double.MAX_VALUE){
                return new NodeDrawingInfo(true, Color.BLUE);
            }

            return new NodeDrawingInfo(false, null);
        });
    }

    @Override
    public List<Integer> getLandmarksUsedTo() {
        return landmarks;
    }

    @Override
    public List<Integer> getLandmarksUsedFrom() {
        return null;
    }

    @Override
    public void SetLandmarkSubsetSize(int i) {
        this.landmarkSubsetSize = i;
    }

    private List<Node> retraceSteps(Node source){
        List<Node> res = new ArrayList<>();

        Node currNode = currTarget;
        while (currNode != source){
            res.add(currNode);
            currNode = currNode.getPredecessor();

            if(currNode == null){
                //return null if impossible
                return null;
            }
        }

        res.add(source);
        Collections.reverse(res);

        return res;
    }

    private double h(Node n, int landmark){
        //return n.getDistancesToLandmarks().get(landmark) - currTarget.getDistancesToLandmarks().get(landmark);
        double temp = Math.max(n.getDistancesToLandmarks()[landmark] - currTarget.getDistancesToLandmarks()[landmark], currTarget.getDistancesFromLandmarks()[landmark] - n.getDistancesFromLandmarks()[landmark]);
        return Math.max(temp, distance(n, currTarget));
    }//-currTarget.getLandmarkDistancesFromLandmark().get(landmark) + n.getLandmarkDistancesFromLandmark().get(landmark);

    private double distance(Node n1, Node n2){
        return Math.sqrt(Math.pow(n1.getX()-n2.getX(), 2) + Math.pow(n1.getY()-n2.getY(), 2));
    }

    private void clearDistanceAndPredecessor(){
        Map<Long, Node> nodeMap = graph.getNodeMap();
        nodeMap.values().forEach(node -> {
            node.setDistance(Double.MAX_VALUE);
            node.setPredecessor(null);
            node.setVisited(false);

        });
    }
}
