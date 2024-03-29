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
public class LandmarkBiDirModified extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> priorityQueueForward;
    private PriorityQueue<NodeWrapper> priorityQueueBackward;

    private int landmarkSubsetSize = 2;
    private int defaultLandmarkAmount = 20;
    private List<Integer> activeLandmarks;

    private double shortestDistance;
    private Node meet = null;

    public LandmarkBiDirModified(Graph graph){
        this(graph, 20);
    }

    public LandmarkBiDirModified(Graph graph, int defaultLandmarkAmount){
        super(graph);
        priorityQueueForward = new PriorityQueue<>();
        priorityQueueBackward = new PriorityQueue<>();
        activeLandmarks = new ArrayList<>();
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
        activeLandmarks.clear();
        clearDistanceAndPredecessor();
        priorityQueueForward.clear();
        priorityQueueBackward.clear();
        nodesVisited = 0;
        nodesScanned = 0;

        long start1 = System.currentTimeMillis();
        //Sanity checks
        for(Integer i : activeLandmarks) {
            if(source.getDistancesToLandmarks()[i] == Double.MAX_VALUE && destination.getDistancesToLandmarks()[i] < Double.MAX_VALUE){
                setExecutionTimeFromStart(start1);
                return null; //No path exists.

            }

            if(destination.getDistancesFromLandmarks()[i] == Double.MAX_VALUE && source.getDistancesFromLandmarks()[i] < Double.MAX_VALUE){
                setExecutionTimeFromStart(start1);
                return null; //No path exists.
            }
        }

        //Fallback check
        if(!canSeeLandmarks(source) || !canSeeLandmarks(destination)){
            PathFinder fallBackPF = new AStarImplBiDirImpl(graph);
            List<Node> fallbackPath = fallBackPF.getShortestPath(source, destination);
            nodesVisited = fallBackPF.getNodesVisited();
            nodesScanned = fallBackPF.getNodesScanned();
            executionTime = fallBackPF.getLastExecutionTime();
            return fallbackPath;
        }

        //Initial setup
        long start = System.currentTimeMillis();

        if(source == destination){
            setExecutionTimeFromStart(start);
            return Collections.singletonList(source);
        }

        FindLandmarkSubsetForward(source, destination);
        FindLandmarkSubsetBackward(source, destination);

        /*System.out.println(source.getId());
        System.out.println(destination.getId());
        System.out.println("Subset size " + landmarksBackward.size());*/


        source.setDistance(0);
        destination.setDistance2(0);
        priorityQueueForward.add(new NodeWrapperImpl(source, 0));
        priorityQueueBackward.add(new NodeWrapperImpl(destination, 0));

        //Sanity checks


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

            if(!currNodeWBack.getNode().getVisited2()) {
                nodesVisited += 1;
                handleBackwardsPass(source, destination, currNodeWBack);
            }

        }


        //System.out.println("A* Bidirectional took " + (this.executionTime) + " ms");

        List<Node> path = null;
        if(meet != null) {
            path = retraceSteps(source, destination, meet);
        }

        setExecutionTimeFromStart(start);

        return path; //Meet never found, return null
    }

    private void setExecutionTimeFromStart(long start) {
        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
    }

    private boolean canSeeLandmarks(Node n){
        boolean t1 = false;
        boolean t2 = false;

        for (int i = 0; i < n.getDistancesToLandmarks().length; i++) {
            if(n.getDistancesToLandmarks()[i] < Double.MAX_VALUE/2){
                t1 = true;
                break;
            }
        }

        for (int i = 0; i < n.getDistancesFromLandmarks().length; i++) {
            if(n.getDistancesFromLandmarks()[i] < Double.MAX_VALUE/2){
                t2 = true;
                break;
            }
        }

        return t1 && t2;
    }


    private void FindLandmarkSubsetForward(Node source, Node destination) {
        //really slow, but easy to read
        //also is only run once when getting shortest path
        //amount of landmarks and subsetsize are also low

        int landmark;
        int bestLandmark;
        double h = 0;
        for(int j = 0; j < landmarkSubsetSize; j++){
            bestLandmark = -1;
            h = -Double.MAX_VALUE;
            for(int i = 0; i < source.getDistancesFromLandmarks().length; i++){
                if(!activeLandmarks.contains(i)){
                    landmark = i;
                    double next = hTo(source, destination, landmark);
                    if(h < next){
                        bestLandmark = landmark;
                        h = next;
                    }
                }
            }
            if(bestLandmark != -1 ) {
                activeLandmarks.add(bestLandmark);
            }
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
            bestLandmark = -1;
            h = -Double.MAX_VALUE;
            for(int i = 0; i < destination.getDistancesFromLandmarks().length; i++){
                if(!activeLandmarks.contains(i)){
                    landmark = i;
                    double next = hFrom(destination, source, landmark);
                    if(h < next){
                        bestLandmark = landmark;
                        h = next;
                    }
                }
            }
            if(bestLandmark != -1 ) {
                activeLandmarks.add(bestLandmark);
            }
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
        double h = -Double.MAX_VALUE;
        for(int i : activeLandmarks){
            double curr = hTo(currNode, destination, i);
            double curr2 = hFrom(destination, currNode, i);
            if(h < curr){
                h = curr;
            }
            if(h < curr2){
                h = curr2;
            }
        }
        return h;
    }

    private double findBackwardLowerBound(Node source, Node destination, Node currNode){
        double h = -Double.MAX_VALUE;
        for(int i : activeLandmarks){
            double curr = hFrom(currNode, source, i);
            double curr2 = hTo(source, currNode, i);
            if(h < curr){
                h = curr;
            }
            if(h < curr2){
                h = curr2;
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
        return activeLandmarks;
    }

    @Override
    public List<Integer> getLandmarksUsedFrom() {
        return activeLandmarks;
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

    private double hTo(Node curr, Node target, int landmark) {
        //if(curr.getDistancesToLandmarks().get(landmark) == Double.MAX_VALUE){ System.out.println("Curr l for " + curr.getDistancesToLandmarks().get(landmark));}
        //if(target.getDistancesToLandmarks().get(landmark) == Double.MAX_VALUE){ System.out.println("target l for " + target.getDistancesToLandmarks().get(landmark));}

        return curr.getDistancesToLandmarks()[landmark] - target.getDistancesToLandmarks()[landmark];
    }

    private double hFrom(Node curr, Node target, int landmark) {
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
