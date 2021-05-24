package openmap.alternative_pathfinders;

import openmap.framework.*;
import openmap.gui.NodeDrawingInfo;
import openmap.landmark_selection.FarthestLandmarkSelectionImpl;
import openmap.standard.AbstractPathfinder;
import openmap.standard.NodeWrapperImpl;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Consistent bidirectional implementation of landmarks. Quite fast in most circumstances.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class LandmarkBiDirConsistentImpl extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> priorityQueueForward;
    private PriorityQueue<NodeWrapper> priorityQueueBackward;

    private int landmarkSubsetSize = 2;
    private int defaultLandmarkAmount = 20;
    private List<Integer> landmarksForward;
    private List<Integer> landmarksBackward;

    private double shortestDistance;
    private Node meet = null;

    public LandmarkBiDirConsistentImpl(Graph graph){
        this(graph, 20);
    }

    public LandmarkBiDirConsistentImpl(Graph graph, int defaultLandmarkAmount){
        super(graph);
        priorityQueueForward = new PriorityQueue<>();
        priorityQueueBackward = new PriorityQueue<>();
        landmarksForward = new ArrayList<>();
        landmarksBackward = new ArrayList<>();
        this.defaultLandmarkAmount = defaultLandmarkAmount;
    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {
        if(source.getDistancesToLandmarks().length == 0) {
            System.out.println("Attempt to get path from landmarks without landmarks, using the default setting of farthest landmarks with k = 20");
            FarthestLandmarkSelectionImpl fls = new FarthestLandmarkSelectionImpl(graph);
            fls.findLandmarks(this.defaultLandmarkAmount);
        }

        //Prepare for run
        landmarksForward.clear();
        landmarksBackward.clear();
        clearDistanceAndPredecessor();
        priorityQueueForward.clear();
        priorityQueueBackward.clear();

        //Initial setup
        long start = System.currentTimeMillis();
        nodesVisited = 0;
        nodesScanned = 0;

        if(source == destination){
            setExecutionTimeFromStart(start);
            return Collections.singletonList(source);
        }

        FindLandmarkSubsetForward(source, destination);
        FindLandmarkSubsetBackward(source, destination);


        source.setDistance(0);
        destination.setDistance2(0);
        priorityQueueForward.add(new NodeWrapperImpl(source, 0));
        priorityQueueBackward.add(new NodeWrapperImpl(destination, 0));

        //Sanity checks
        for(Integer i : landmarksForward) {
            if(source.getDistancesToLandmarks()[i] == Double.MAX_VALUE && destination.getDistancesToLandmarks()[i] < Double.MAX_VALUE){
                setExecutionTimeFromStart(start);
                return null; //No path exists.
            }

            if(destination.getDistancesFromLandmarks()[i] == Double.MAX_VALUE && source.getDistancesFromLandmarks()[i] < Double.MAX_VALUE){
                setExecutionTimeFromStart(start);
                return null; //No path exists.
            }
        }

        meet = null;
        shortestDistance = Double.MAX_VALUE;
        NodeWrapper currNodeWFor, currNodeWBack;
        while (!priorityQueueForward.isEmpty() && !priorityQueueBackward.isEmpty()){ //If one is empty, path does not exist
            currNodeWFor = priorityQueueForward.poll();
            currNodeWBack = priorityQueueBackward.poll();


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

        setExecutionTimeFromStart(start);
        //System.out.println("A* Bidirectional took " + (this.executionTime) + " ms");

        if(meet != null) {
            return retraceSteps(source, destination, meet);
        }
        return null; //Meet never found, return null
    }

    private void setExecutionTimeFromStart(long start) {
        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
    }


    private void FindLandmarkSubsetForward(Node source, Node destination) {
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
                if(!landmarksForward.contains(i)){
                    landmark = i;
                    double next = hForward(source, destination, landmark);
                    double test = hBackward(destination, source, landmark);
                    if(h < next){
                        bestLandmark = landmark;
                        h = next;
                    }
                }
            }
            landmarksForward.add(bestLandmark);
        }
    }

    private void FindLandmarkSubsetBackward(Node source, Node destination) {
        //really slow, but easy to read
        //also is only run once when getting shortest path
        //amount of landmarks and subsetsize are also low

        int landmark;
        int bestLandmark;
        double h = 0;
        for(int j = 0; j < landmarkSubsetSize; j++){
            bestLandmark = 0;
            h = 0;
            for(int i = 0; i < destination.getDistancesFromLandmarks().length; i++){
                if(!landmarksBackward.contains(i)){
                    landmark = i;
                    double next = hBackward(destination, source, landmark);
                    double test = hForward(source, destination, landmark);
                    if(h < next){
                        bestLandmark = landmark;
                        h = next;
                    }
                }
            }
            landmarksBackward.add(bestLandmark);
        }
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
        double testDist = pathDest.getDistance2() + pathDest.getDistance(); //+ pForward(source, destination, pathDest) + pBackward(source, destination, pathDest); //Pt + Ps should be zero
        //newDistance + path.getDestination().getDistance2()
        if (testDist < shortestDistance) {
            shortestDistance = testDist;
            meet = pathDest;
        }
    }

    private double pForward(Node source, Node destination, Node currNode) {
        double forwardBound = findForwardLowerBound(source, destination, currNode);
        double backwardBound = findBackwardLowerBound(source, destination, currNode);
        return 0.5 * (forwardBound - backwardBound);
    }

    private double pBackward(Node source, Node destination, Node currNode) {
        return -pForward(source, destination, currNode);
                //0.5 * (h(source, pathDest) - h(destination, pathDest)); //Old manual rewrite...
    }

    private double findForwardLowerBound(Node source, Node destination, Node currNode){
        double h = 0;
        for(int i : landmarksForward){
            double curr = hForward(currNode, destination, i);
            if(h < curr){
                h = curr;
            }
        }
        return h;
    }

    private double findBackwardLowerBound(Node source, Node destination, Node currNode){
        double h = 0;
        for(int i : landmarksBackward){
            double curr = hBackward(currNode, source, i);
            if(h < curr){
                h = curr;
            }
        }
        return h;
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
        return landmarksForward;
    }

    @Override
    public List<Integer> getLandmarksUsedFrom() {
        return landmarksBackward;
    }

    @Override
    public void SetLandmarkSubsetSize(int i) {
        this.landmarkSubsetSize = i;
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

    private double hForward(Node curr, Node target, int landmark) {
        //if(curr.getDistancesToLandmarks().get(landmark) == Double.MAX_VALUE){ System.out.println("Curr l for " + curr.getDistancesToLandmarks().get(landmark));}
        //if(target.getDistancesToLandmarks().get(landmark) == Double.MAX_VALUE){ System.out.println("target l for " + target.getDistancesToLandmarks().get(landmark));}


        return curr.getDistancesToLandmarks()[landmark] - target.getDistancesToLandmarks()[landmark];
    }

    private double hBackward(Node curr, Node target, int landmark) {
        //System.out.println(landmark);
        //System.out.println(curr.getId());
        //System.out.println(curr.getDistancesFromLandmarks());
        //if(curr.getDistancesFromLandmarks().get(landmark) == Double.MAX_VALUE){ System.out.println("Curr l back " + curr.getDistancesFromLandmarks().get(landmark));}
        //if(target.getDistancesToLandmarks().get(landmark) == Double.MAX_VALUE){ System.out.println("target l back " + target.getDistancesToLandmarks().get(landmark));}

        return  curr.getDistancesFromLandmarks()[landmark] - target.getDistancesFromLandmarks()[landmark];
                //curr.getDistancesFromLandmarks().get(landmark) - target.getDistancesToLandmarks().get(landmark);
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
