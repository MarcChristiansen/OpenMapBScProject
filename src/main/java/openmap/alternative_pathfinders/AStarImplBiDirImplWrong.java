package openmap.alternative_pathfinders;

import openmap.framework.*;
import openmap.gui.NodeDrawingInfo;
import openmap.standard.NodeWrapperImpl;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarImplBiDirImplWrong implements PathFinder {

    private Graph graph;
    private PriorityQueue<NodeWrapper> priorityQueueForward;
    private PriorityQueue<NodeWrapper> priorityQueueBackward;

    private long executionTime;
    private double shortestDistance;
    private Node meet = null;

    public AStarImplBiDirImplWrong(Graph graph){
        this.graph = graph;
        this.executionTime = 0;
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
        long start = System.currentTimeMillis();
        source.setDistance(0);
        destination.setDistance2(0);
        priorityQueueForward.add(new NodeWrapperImpl(source, Pt(source, destination, destination)));
        priorityQueueBackward.add(new NodeWrapperImpl(destination, Ps(source, destination, source)));

        meet = null;
        shortestDistance = Double.MAX_VALUE;
        NodeWrapper currNodeWFor = null;
        NodeWrapper currNodeWBack = null;
        Node temp;
        while (!priorityQueueForward.isEmpty() && !priorityQueueBackward.isEmpty()){ //If one is empty, path does not exist
            currNodeWFor = priorityQueueForward.poll();
            currNodeWBack = priorityQueueBackward.poll();

            //System.out.println(currNodeWFor.getDist());
            //System.out.println(currNodeWBack.getDist());
            //System.out.println(shortestDistance);
            if(currNodeWFor.getDist() +  currNodeWBack.getDist() >= shortestDistance){
                //System.out.println("break how???");
                break;
            }
            //System.out.println("hej");


            handleForwardPass(source, destination, currNodeWFor);
            currNodeWFor.getNode().setVisited(true);
            handleBackwardsPass(source, destination, currNodeWBack);
            currNodeWBack.getNode().setVisited2(true);

            //System.out.println("hej2");




        }
        //System.out.println((!priorityQueueForward.isEmpty() && !priorityQueueBackward.isEmpty()));

        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
        //System.out.println("A* Bidirectional took " + (this.executionTime) + " ms");

        if(meet != null) {
            return retraceSteps(source, destination, meet);
        }
        return null; //Meet never found, return null
    }

    private Node handleForwardPass(Node source, Node destination, NodeWrapper currNodeW) {
        for (Path p: currNodeW.getNode().getOutgoingPaths()) {
            double totalWeight = currNodeW.getNode().getDistance()+p.getWeight();
            Node pathDest = p.getDestination();

            //If the newly discovered node has not been handled before or we found a shorter path to it
            if(totalWeight < pathDest.getDistance()) {
                pathDest.setDistance(totalWeight);
                pathDest.setPredecessor(currNodeW.getNode());

                priorityQueueForward.add(new NodeWrapperImpl(pathDest, totalWeight+ Ps(source, destination, pathDest)));
            }

            if(pathDest.getVisited2()){
                //newDistance + path.getDestination().getDistance2()
                if(pathDest.getDistance2() + pathDest.getDistance() < shortestDistance){
                    shortestDistance = pathDest.getDistance2() + pathDest.getDistance();
                    meet = pathDest;
                }
            }
        }
        return null;
    }



    private Node handleBackwardsPass(Node source, Node destination, NodeWrapper currNodeW) {
        for (Path p: currNodeW.getNode().getIncomingPaths()) {
            double totalWeight = currNodeW.getNode().getDistance2()+p.getWeight();
            Node pathDest = p.getSource();

            //If the newly discovered node has not been handled before or we found a shorter path to it
            if(totalWeight < pathDest.getDistance2()) {
                pathDest.setDistance2(totalWeight);
                pathDest.setPredecessor2(currNodeW.getNode());
                priorityQueueBackward.add(new NodeWrapperImpl(pathDest, totalWeight + Pt(source, destination, pathDest)));
            }

            if(pathDest.getVisited()){
                //newDistance + path.getDestination().getDistance2()
                if(pathDest.getDistance2() + pathDest.getDistance() < shortestDistance){
                    shortestDistance = pathDest.getDistance2() + pathDest.getDistance();
                    meet = pathDest;
                }
            }
        }


        return null;
    }

    private double Pt(Node source, Node destination, Node pathDest) {
        return 0.5 * (h(destination, pathDest) - h(source, pathDest));
    }

    private double Ps(Node source, Node destination, Node pathDest) {
        return 0.5 * (h(source, pathDest) - h(destination, pathDest));
    }



    @Override
    public long getLastExecutionTime() {
        return executionTime;
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

    @Override
    public List<Integer> getLandmarksUsedTo() {
        return null;
    }

    @Override
    public List<Integer> getLandmarksUsedFrom() {
        return null;
    }

    @Override
    public void SetLandmarkSubsetSize(int i) { }

    private List<Node> retraceSteps(Node source, Node target, Node meet){
        List<Node> stom = new ArrayList<>();
        List<Node> mtot = new ArrayList<>();

        Node currNode = meet;
        while (currNode != source){
            stom.add(currNode);
            currNode = currNode.getPredecessor();

            if(currNode == null){
                //return null if impossible
                return null;
            }
        }

        stom.add(source);
        Collections.reverse(stom);

        if(meet != target){
            currNode = meet.getPredecessor2();
        }
        else{
            currNode = meet; //Edge case that meet is the target
        }
        while (currNode != target){
            mtot.add(currNode);
            currNode = currNode.getPredecessor2();

            if(currNode == null){
                //return null if impossible
                return null;
            }
        }

        if(meet != target) {
            mtot.add(target);
        }


        List<Node> res = Stream.concat(stom.stream(), mtot.stream())
                .collect(Collectors.toList());

        return res;
    }

    private double p(Node NPos, Node Nneg, Node curr){
        return (distance(NPos, curr) - distance(Nneg, curr))/2;
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
