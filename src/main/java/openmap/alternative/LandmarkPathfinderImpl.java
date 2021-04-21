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

    private Node currTarget;
    private long executionTime;
    private int landmark;

    private boolean preProcessDone;

    public LandmarkPathfinderImpl(Graph graph){
        this.graph = graph;
        this.executionTime = 0;
        priorityQueue = new PriorityQueue<NodeWrapper>();

    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {
        if(!preProcessDone){
            LandmarkSelection landmarkSelection = new RandomizedLandmarkSelectionImpl();
            List<Node> landmarkList = landmarkSelection.findLandmarks(graph, 20);
            landmarkSelection.preProcessNodes(graph, landmarkList);
            preProcessDone = true;
        }

        //Prepare for A* run
        clearDistanceAndPredecessor();
        priorityQueue.clear();

        //Initial setup
        long start = System.currentTimeMillis();
        List<Node> path = null;
        currTarget = destination;

        //pick best landmark to work from
        int bestLandmark = 0;
        double h = 0;
        for(int i = 0; i < source.getLandmarkDistances().size(); i++){
            landmark = i;
            if(h < h(source)){
                bestLandmark = landmark;
                h = h(source);
            }
        }
        landmark = bestLandmark;

        source.setDistance(0);
        priorityQueue.add(new NodeWrapperImpl(source, h(source)));

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
                    priorityQueue.add(new NodeWrapperImpl(pathDest, totalWeight+h(pathDest)));
                }
            }
        }

        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
        System.out.println("Landmark took " + (this.executionTime) + " ms");

        return path;
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

    private double h(Node n){
        return distance(n, currTarget);
    }

    private double distance(Node n, Node currTarget) {
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
