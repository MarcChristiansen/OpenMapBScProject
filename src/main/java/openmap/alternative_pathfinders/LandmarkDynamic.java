package openmap.alternative_pathfinders;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.NodeWrapper;
import openmap.framework.Path;
import openmap.gui.NodeDrawingInfo;
import openmap.landmark_selection.FarthestLandmarkSelectionImpl;
import openmap.standard.AbstractPathfinder;
import openmap.standard.NodeWrapperImpl;
import org.apache.commons.lang3.NotImplementedException;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

/**
 * Landmark based pathfinding. Works in a similar way to A* but with a modified heuristic.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class LandmarkDynamic extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> priorityQueue;

    private Node currTarget;
    private int defaultLandmarkAmount = 20;
    private List<Integer> landmarks;

    public LandmarkDynamic(Graph graph){
        this(graph, 20);
    }

    public LandmarkDynamic(Graph graph, int defaultLandmarkAmount){
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
        int landmarkRescanCounter = 0;
        while (!priorityQueue.isEmpty()){
            currNodeW = priorityQueue.poll();
            landmarkRescanCounter++;

            if(landmarkRescanCounter % 2500 == 0){
                if(dynLandmark(currNodeW.getNode())){
                    List<NodeWrapper> tempList= new ArrayList<>(priorityQueue);
                    tempList.add(currNodeW);
                    priorityQueue.clear();
                    for (NodeWrapper nw: tempList) {
                        double potential = getLowerbound(nw.getNode());
                        priorityQueue.add(new NodeWrapperImpl(nw.getNode(), nw.getNode().getDistance()+potential));
                    }
                    currNodeW = priorityQueue.poll();
                }
            }


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

    private boolean dynLandmark(Node n){
        int newL = -1;
        double best = getLowerbound(n);

        for(int i = 0; i < n.getDistancesToLandmarks().length; i++){
            if(landmarks.contains(i)){continue;}
            double test = hTo(n, i);

            if(test > best){
                newL = i;
                best = test;
            }
        }

        for(int i = 0; i < n.getDistancesFromLandmarks().length; i++){
            if(landmarks.contains(i)){continue;}
            double test = hFrom(n, i);

            if(test > best){
                newL = i;
                best = test;
            }
        }

        if(newL > -1){
            landmarks.add(newL);
            return true;
        }

        return false;
    }


    private void setExecutionTimeFromStart(long start) {
        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
    }

    private double getLowerbound(Node currNode) {
        double h = 0;
        double temp = 0;
        double temp2 = 0;
        for(int i : landmarks){
            temp = hTo(currNode, i);
            if(h < temp){
                h = temp;
            }

            temp2 =  hFrom(currNode, i);
            if(h < temp2){
                h = temp2;
            }
        }
        return h;
    }

    private void FindLandmarkSubset(Node source) {
        //really slow, but easy to read
        //also is only run once when getting shortest path
        //amount of landmarks and subsetsize are also low

        int bestLandmark;
        double best = -Double.MAX_VALUE;
        double temp = 0;
        bestLandmark = 0;
        for(int i = 0; i < source.getDistancesToLandmarks().length; i++){
            if(!landmarks.contains(i)){
                temp = source.getDistancesToLandmarks()[i] - currTarget.getDistancesToLandmarks()[i];
                if(best < temp){
                    bestLandmark = i;
                    best = temp;
                }
            }
        }

        for(int i = 0; i < source.getDistancesFromLandmarks().length; i++){
            if(!landmarks.contains(i)){
                temp = currTarget.getDistancesFromLandmarks()[i] - source.getDistancesFromLandmarks()[i];
                if(best < temp){
                    bestLandmark = i;
                    best = temp;
                }
            }
        }
        landmarks.add(bestLandmark);
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
        return landmarks;
    }

    @Override
    public void SetLandmarkSubsetSize(int i) {
        throw new NotImplementedException("This method uses dynamic selection");
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

    private double hTo(Node n, int landmark){
        //return n.getDistancesToLandmarks().get(landmark) - currTarget.getDistancesToLandmarks().get(landmark);
        return n.getDistancesToLandmarks()[landmark] - currTarget.getDistancesToLandmarks()[landmark];
    }//-currTarget.getLandmarkDistancesFromLandmark().get(landmark) + n.getLandmarkDistancesFromLandmark().get(landmark);

    private double hFrom(Node n, int landmark){
        //return n.getDistancesToLandmarks().get(landmark) - currTarget.getDistancesToLandmarks().get(landmark);
        return currTarget.getDistancesFromLandmarks()[landmark] - n.getDistancesFromLandmarks()[landmark];
    }//-currTarget.getLandmarkDistancesFromLandmark().get(landmark) + n.getLandmarkDistancesFromLandmark().get(landmark);


    private void clearDistanceAndPredecessor(){
        Map<Long, Node> nodeMap = graph.getNodeMap();
        nodeMap.values().forEach(node -> {
            node.setDistance(Double.MAX_VALUE);
            node.setPredecessor(null);
            node.setVisited(false);

        });
    }
}
