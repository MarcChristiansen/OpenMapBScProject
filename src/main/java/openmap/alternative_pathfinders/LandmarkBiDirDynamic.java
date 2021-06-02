package openmap.alternative_pathfinders;

import openmap.framework.*;
import openmap.gui.NodeDrawingInfo;
import openmap.landmark_selection.FarthestLandmarkSelectionImpl;
import openmap.landmark_selection.FarthestLandmarkSelectionImplSame;
import openmap.standard.AbstractPathfinder;
import openmap.standard.NodeWrapperImpl;
import org.apache.commons.lang3.NotImplementedException;

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
public class LandmarkBiDirDynamic extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> priorityQueueForward;
    private PriorityQueue<NodeWrapper> priorityQueueBackward;

    private int defaultLandmarkAmount = 20;
    private List<Integer> landmarks;

    private double shortestDistance;
    private Node meet = null;

    private double betterLandmarkConst = 0.01;

    public LandmarkBiDirDynamic(Graph graph){
        this(graph, 20);
    }

    public LandmarkBiDirDynamic(Graph graph, int defaultLandmarkAmount){
        super(graph);
        priorityQueueForward = new PriorityQueue<>();
        priorityQueueBackward = new PriorityQueue<>();
        landmarks = new ArrayList<>();
        this.defaultLandmarkAmount = defaultLandmarkAmount;
    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {

        if(source.getDistancesToLandmarks().length == 0) {
            System.out.println("Attempt to get path from landmarks without landmarks, using the default setting of farthest landmarks with k = 20 dyn");
            FarthestLandmarkSelectionImplSame fls = new FarthestLandmarkSelectionImplSame(graph);
            fls.findLandmarks(this.defaultLandmarkAmount);
        }

        //Prepare for run
        landmarks.clear();
        clearDistanceAndPredecessor();
        priorityQueueForward.clear();
        priorityQueueBackward.clear();
        nodesVisited = 0;
        nodesScanned = 0;

        long start1 = System.currentTimeMillis();
        //Sanity checks
        for(Integer i : landmarks) {
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

        FindInitialSubset(source, destination);

        /*System.out.println(source.getId());
        System.out.println(destination.getId());
        System.out.println("Subset size " + landmarksBackward.size());*/


        source.setDistance(0);
        destination.setDistance2(0);
        priorityQueueForward.add(new NodeWrapperImpl(source, 0));
        priorityQueueBackward.add(new NodeWrapperImpl(destination, 0));

        meet = null;
        shortestDistance = Double.MAX_VALUE;
        NodeWrapper currNodeWFor, currNodeWBack;
        int recalculationCounter = 1;
        while (!priorityQueueForward.isEmpty() && !priorityQueueBackward.isEmpty()){ //If one is empty, path does not exist
            currNodeWFor = priorityQueueForward.poll();
            currNodeWBack = priorityQueueBackward.poll();
            recalculationCounter++;

            if(recalculationCounter % 1250 == 0){

                if(dynLandmark(currNodeWFor.getNode(), currNodeWBack.getNode(), source, destination)){
                    //System.out.println("recalculation");
                    List<NodeWrapper> tempList= new ArrayList<>(priorityQueueForward);
                    tempList.add(currNodeWFor);
                    priorityQueueForward.clear();
                    for (NodeWrapper nw: tempList) {
                        double potential = pForward(source, destination, nw.getNode());
                        //if(!nw.getNode().getVisited()) {
                            priorityQueueForward.add(new NodeWrapperImpl(nw.getNode(), nw.getNode().getDistance() + potential));
                        //}
                    }

                    tempList = new ArrayList<>(priorityQueueBackward);
                    tempList.add(currNodeWBack);
                    priorityQueueBackward.clear();
                    for (NodeWrapper nw: tempList) {
                        double potential = pBackward(source, destination, nw.getNode());
                        //if(!nw.getNode().getVisited2()){
                            priorityQueueBackward.add(new NodeWrapperImpl(nw.getNode(), nw.getNode().getDistance2()+potential));
                        //}
                    }

                    currNodeWFor = priorityQueueForward.poll();
                    currNodeWBack = priorityQueueBackward.poll();
                }
            }

            /*System.out.println(pForward(source, destination, destination));
            System.out.println(pBackward(source, destination, source));
            System.out.println(getLowerBoundFor(source, source));
            System.out.println(getLowerBoundBack(destination, destination));*/

            if(currNodeWFor.getDist() +  currNodeWBack.getDist() >= shortestDistance + pForward(source, destination, source)){
                break;
            }


            //System.out.println(pForward(source, destination, source));

            if(!currNodeWFor.getNode().getVisited()){
                nodesVisited += 1;
                handleForwardPass(source, destination, currNodeWFor);
            }

            if(!currNodeWBack.getNode().getVisited2()) {
                nodesVisited += 1;
                handleBackwardsPass(source, destination, currNodeWBack);
            }

        }

        List<Node> path = null;
        if(meet != null) {
            path = retraceSteps(source, destination, meet);
        }

        setExecutionTimeFromStart(start);

        return path; //Meet never found, return null
    }

    private boolean dynLandmark(Node nodeF, Node nodeB, Node source, Node destination) {
        int newL = -1;
        double best = Math.max(getLowerBound(nodeF, destination), getLowerBound(source, nodeB)) + betterLandmarkConst;

        for(int i = 0; i < source.getDistancesToLandmarks().length; i++){
            if(landmarks.contains(i)) {continue;}
            double test = Math.max(hTo(nodeF, destination,i), hFrom(nodeF, destination,i));
            double test2 = Math.max(hTo(source, nodeB,i), hFrom(source, nodeB,i));

            if(test > best || test2 > best){
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


    private void FindInitialSubset(Node source, Node destination) {
        //really slow, but easy to read
        //also is only run once when getting shortest path
        //amount of landmarks and subsetsize are also low

        int landmark;
        int bestLandmark;
        double best = -Double.MAX_VALUE;
        double temp = -Double.MAX_VALUE;
        bestLandmark = 0;
        for(int i = 0; i < source.getDistancesToLandmarks().length; i++){
            if(!landmarks.contains(i)){
                landmark = i;
                temp = hTo(source, destination, i);
                if(best < temp){
                    bestLandmark = landmark;
                    best = temp;
                }
            }
        }
        landmarks.add(bestLandmark);

        best = -Double.MAX_VALUE;
        temp = -Double.MAX_VALUE;
        for(int i = 0; i < source.getDistancesFromLandmarks().length; i++){
            if(!landmarks.contains(i)){
                landmark = i;
                temp = hFrom(source, destination, i);
                if(best < temp){
                    bestLandmark = landmark;
                    best = temp;
                }
            }
        }
        landmarks.add(bestLandmark);
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
        double testDist = pathDest.getDistance2() + pathDest.getDistance();
        //newDistance + path.getDestination().getDistance2()

        //System.out.println(pForward(source, destination, pathDest));
        //System.out.println(pBackward(source, destination, pathDest));
        if (testDist < shortestDistance) {
            shortestDistance = testDist;
            meet = pathDest;
        }
    }

    private double pForward(Node source, Node destination, Node currNode) {
        return pNorm(source, destination, currNode) + 0.5 * getLowerBound(source, destination);
    }

    private double pNorm(Node source, Node destination, Node currNode) {
        double forwardBound = getLowerBound(currNode, destination);
        double backwardBound = getLowerBound(source, currNode);
        return 0.5 * (forwardBound - backwardBound);
    }

    private double pBackward(Node source, Node destination, Node currNode) {
        return -pNorm(source, destination, currNode) + 0.5 * getLowerBound(source, destination);
                //0.5 * (h(source, pathDest) - h(destination, pathDest)); //Old manual rewrite...
    }

    private double getLowerBound(Node currNode, Node destination) {
        double h = -Double.MAX_VALUE;
        double temp = -Double.MAX_VALUE;
        double temp2 = -Double.MAX_VALUE;
        for(int i : landmarks){
            temp = hTo(currNode, destination, i);
            if(h < temp){
                h = temp;
            }

        }

        for(int i : landmarks){
            temp2 =  hFrom(currNode, destination, i);

            if(h < temp2){
                h = temp2;
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
        return landmarks;
    }

    @Override
    public List<Integer> getLandmarksUsedFrom() {
        return landmarks;
    }

    @Override
    public void SetLandmarkSubsetSize(int i) {
        throw new NotImplementedException("Dynamic does not use subset size");
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
        return curr.getDistancesToLandmarks()[landmark] - target.getDistancesToLandmarks()[landmark];
    }

    private double hFrom(Node curr, Node target, int landmark) {
        return  target.getDistancesFromLandmarks()[landmark] - curr.getDistancesFromLandmarks()[landmark];
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
