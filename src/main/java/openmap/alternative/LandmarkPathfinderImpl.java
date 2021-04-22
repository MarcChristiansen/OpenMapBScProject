package openmap.alternative;

import openmap.framework.*;
import openmap.gui.NodeDrawingInfo;
import openmap.standard.NodeWrapperImpl;
import openmap.standard.RandomizedLandmarkSelectionImpl;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class LandmarkPathfinderImpl implements PathFinder {

    private Graph graph;
    private PriorityQueue<NodeWrapper> priorityQueue;

    private Node source;
    private Node currTarget;
    private long executionTime;
    private int landmarkSubsetSize = 5;
    private List<Integer> landmarks;

    private boolean preProcessDone;

    public LandmarkPathfinderImpl(Graph graph){
        this.graph = graph;
        this.executionTime = 0;
        priorityQueue = new PriorityQueue<NodeWrapper>();
        landmarks = new ArrayList<Integer>();

    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {

        //Prepare for A* run
        clearDistanceAndPredecessor();
        priorityQueue.clear();

        //Initial setup
        long start = System.currentTimeMillis();
        List<Node> path = null;
        currTarget = destination;

        //pick best landmark to work from
        FindLandmarkSubset(source);

        source.setDistance(0);

        //find largest h
        double h = getLowerbound(source);

        priorityQueue.add(new NodeWrapperImpl(source, h));

        NodeWrapper currNodeW = null;
        while (!priorityQueue.isEmpty()){
            currNodeW = priorityQueue.poll();

            if(currNodeW.getNode() == currTarget){
                path = retraceSteps(source);
                break;
            }

            for (Path p: currNodeW.getNode().getOutgoingPaths()) {
                double totalWeight = currNodeW.getNode().getDistance()+p.getWeight();
                Node pathDest = p.getDestination();

                //If the newly discovered node has not been handled before or we found a shorter path to it
                if(totalWeight < pathDest.getDistance()) {
                    pathDest.setDistance(totalWeight);
                    pathDest.setPredecessor(currNodeW.getNode());
                    h = getLowerbound(pathDest);
                    priorityQueue.add(new NodeWrapperImpl(pathDest, totalWeight+h));
                }
            }
        }

        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
        System.out.println("Landmark took " + (this.executionTime) + " ms");

        return path;
    }

    private double getLowerbound(Node source) {
        double h = 0;
        for(int i : landmarks){
            if(h < h(source, i)){
                h = h(source, i);
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
            for(int i = 0; i < source.getLandmarkDistances().size(); i++){
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
    public long getLastExecutionTime() {
        return executionTime;
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
    public List<Integer> getLandmarksUsed() {
        return landmarks;
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
        return n.getLandmarkDistances().get(landmark) - currTarget.getLandmarkDistances().get(landmark);
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
