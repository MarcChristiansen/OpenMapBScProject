package openmap.alternative_pathfinders;

import openmap.framework.*;
import openmap.gui.NodeDrawingInfo;
import openmap.standard.AbstractPathfinder;
import openmap.standard.NodeWrapperImpl;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of a consistent bidirectional A* algorithm
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class AStarImplBiDirImpl extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> priorityQueueForward;
    private PriorityQueue<NodeWrapper> priorityQueueBackward;

    private double shortestDistance;
    private Node meet = null;

    public AStarImplBiDirImpl(Graph graph){
        super(graph);
        priorityQueueForward = new PriorityQueue<NodeWrapper>();
        priorityQueueBackward = new PriorityQueue<NodeWrapper>();
    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {

        if(source == destination){
            return Collections.singletonList(source);
        }

        //Prepare for A* run
        clearDistanceAndPredecessor();
        priorityQueueForward.clear();
        priorityQueueBackward.clear();

        //Initial setup
        nodesVisited = 0;
        nodesScanned = 0;
        long start = System.currentTimeMillis();
        source.setDistance(0);
        destination.setDistance2(0);
        priorityQueueForward.add(new NodeWrapperImpl(source, pForward(source, destination, destination)));
        priorityQueueBackward.add(new NodeWrapperImpl(destination, pBackward(source, destination, source)));

        meet = null;
        shortestDistance = Double.MAX_VALUE;
        NodeWrapper currNodeWFor, currNodeWBack;
        while (!priorityQueueForward.isEmpty() && !priorityQueueBackward.isEmpty()){ //If one is empty, path does not exist
            currNodeWFor = priorityQueueForward.poll();
            currNodeWBack = priorityQueueBackward.poll();
            nodesVisited += 2;

            if(currNodeWFor.getDist() +  currNodeWBack.getDist() >= shortestDistance){
                break;
            }


            if(!currNodeWFor.getNode().getVisited()){
                nodesVisited += 1;
                handleForwardPass(source, destination, currNodeWFor);
            }

            if(!currNodeWFor.getNode().getVisited2()) {
                nodesVisited += 1;
                handleBackwardsPass(source, destination, currNodeWBack);
            }

        }

        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
        //System.out.println("A* Bidirectional took " + (this.executionTime) + " ms");

        if(meet != null) {
            return retraceSteps(source, destination, meet);
        }
        return null; //Meet never found, return null
    }

    private void handleForwardPass(Node source, Node destination, NodeWrapper currNodeW) {
        for (Path p: currNodeW.getNode().getOutgoingPaths()) {
            double totalWeight = currNodeW.getNode().getDistance()+p.getWeight();
            Node pathDest = p.getDestination();

            //If the newly discovered node has not been handled before or we found a shorter path to it
            if(totalWeight < pathDest.getDistance()) {
                pathDest.setDistance(totalWeight);
                pathDest.setPredecessor(currNodeW.getNode());

                nodesScanned++;
                priorityQueueForward.add(new NodeWrapperImpl(pathDest, totalWeight + pForward(source, destination, pathDest))); //Pt + Ps should be zero
            }

            if(pathDest.getVisited2()){
                testPathForShortestPath(source, destination, pathDest);
            }
        }

        currNodeW.getNode().setVisited(true);
    }

    private void handleBackwardsPass(Node source, Node destination, NodeWrapper currNodeW) {
        for (Path p: currNodeW.getNode().getIncomingPaths()) {
            double totalWeight = currNodeW.getNode().getDistance2()+p.getWeight();
            Node pathDest = p.getSource();

            //If the newly discovered node has not been handled before or we found a shorter path to it
            if(totalWeight < pathDest.getDistance2()) {
                pathDest.setDistance2(totalWeight);
                pathDest.setPredecessor2(currNodeW.getNode());

                nodesScanned++;
                priorityQueueBackward.add(new NodeWrapperImpl(pathDest, totalWeight + pBackward(source, destination, pathDest))); //Pt + Ps should be zero
            }

            if(pathDest.getVisited()){
                //newDistance + path.getDestination().getDistance2()
                testPathForShortestPath(source, destination, pathDest);
            }
        }

        currNodeW.getNode().setVisited2(true);
    }

    private void testPathForShortestPath(Node source, Node destination, Node pathDest) {
        double testDist = pathDest.getDistance2() + pathDest.getDistance() + pForward(source, destination, pathDest) + pBackward(source, destination, pathDest); //Pt + Ps should be zero
        //newDistance + path.getDestination().getDistance2()
        if (testDist < shortestDistance) {
            shortestDistance = testDist;
            meet = pathDest;
        }
    }

    private double pForward(Node source, Node destination, Node pathDest) {
        return 0.5 * (h(destination, pathDest) - h(source, pathDest));
    }

    private double pBackward(Node source, Node destination, Node pathDest) {
        return -pForward(source, destination, pathDest);
                //0.5 * (h(source, pathDest) - h(destination, pathDest)); //Old manual rewrite...
    }

    @Override
    public Function<Node, NodeDrawingInfo> getVisitedCheckFunction() {
        return ((Node n) -> {
            if(n.getDistance() < Double.MAX_VALUE){
                if(n.getDistance2() < Double.MAX_VALUE) { return new NodeDrawingInfo(true, Color.ORANGE); }
                return new NodeDrawingInfo(true, Color.BLUE);
            }
            if(n.getDistance2() < Double.MAX_VALUE) { return new NodeDrawingInfo(true, Color.GREEN);}

            return new NodeDrawingInfo(false, null);
        });
    }

    private List<Node> retraceSteps(Node source, Node target, Node meet){
        List<Node> sTom = new ArrayList<>();
        List<Node> mTot = new ArrayList<>();

        Node currNode = meet;
        while (currNode != source){
            sTom.add(currNode);
            currNode = currNode.getPredecessor();

            if(currNode == null){ //return null if impossible
                return null;
            }
        }
        sTom.add(source);
        Collections.reverse(sTom);

        //Edge case that meet is the target
        if(meet != target){ currNode = meet.getPredecessor2(); }
        else{
            currNode = meet;
        }

        while (currNode != target){
            mTot.add(currNode);
            currNode = currNode.getPredecessor2();

            if(currNode == null){ //return null if impossible
                return null;
            }
        }

        if(meet != target) {
            mTot.add(target);
        }

        List<Node> res = Stream.concat(sTom.stream(), mTot.stream())
                .collect(Collectors.toList());

        return res;
    }

    private double h(Node source, Node curr){
        return distance(source, curr);
    }

    private double distance(Node n1, Node n2){
        return Math.sqrt(Math.pow(n1.getX()-n2.getX(), 2) + Math.pow(n1.getY()-n2.getY(), 2));
    }

    private void clearDistanceAndPredecessor(){
        Map<Long, Node> nodeMap = graph.getNodeMap();
        nodeMap.values().forEach(node -> {
            node.setDistance(Double.MAX_VALUE);
            node.setPredecessor(null);
            node.setVisited(false);

            node.setDistance2(Double.MAX_VALUE);
            node.setPredecessor2(null);
            node.setVisited2(false);
        });
    }
}
