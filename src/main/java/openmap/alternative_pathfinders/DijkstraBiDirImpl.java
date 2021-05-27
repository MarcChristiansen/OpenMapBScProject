package openmap.alternative_pathfinders;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.framework.NodeWrapper;
import openmap.framework.PathFinder;
import openmap.gui.NodeDrawingInfo;
import openmap.standard.AbstractPathfinder;
import openmap.standard.NodeWrapperImpl;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of a bidirectional Dijkstra algorithm
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class DijkstraBiDirImpl extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> forwardQueue;
    private PriorityQueue<NodeWrapper> backwardQueue;
    private double shortestDistance = Double.MAX_VALUE;
    private Node midNode = null;
    private Long source = null;
    private long executionTime;

    public DijkstraBiDirImpl(Graph graph){
        super(graph);
        forwardQueue = new PriorityQueue<NodeWrapper>();
        backwardQueue = new PriorityQueue<NodeWrapper>();
        //predecessor = new HashMap<Long, Long>();
        //distance = new HashMap<Long, Double>();
        //visited = new HashSet<Node>();

    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {
        clearDistanceAndPredecessor();
        forwardQueue.clear();
        backwardQueue.clear();
        midNode = null;
        shortestDistance = Double.MAX_VALUE;
        nodesScanned = 0;
        nodesVisited = 0;

        //measureable values
        long start = System.currentTimeMillis();
        runBiDir(source, destination);
        List<Node> result = new ArrayList<>();

        Node currNode = destination;
        while(!(currNode == source)){
            result.add(currNode);
            currNode = currNode.getPredecessor();
            if(currNode == null){
                //return null if impossible
                return null;
            }
        }
        result.add(source);
        Collections.reverse(result);
        long finish = System.currentTimeMillis();
        executionTime = finish - start;

        return result;
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

    private void runBiDir(Node source, Node destination) {


        //add source to forward queue with distance 0
        source.setDistance(0);
        source.setPredecessor(source);
        forwardQueue.add(new NodeWrapperImpl(source, source.getDistance()));

        //add destination to backward queue with distance 0
        destination.setDistance2(0);
        destination.setPredecessor2(destination);
        backwardQueue.add(new NodeWrapperImpl(destination, destination.getDistance2()));

        /*
        Node preNode = null;
        Node nextNode = null;
         */

        while (true) {
            //forwards dijkstra
            NodeWrapper forNode = forwardQueue.poll();
            NodeWrapper backNode = backwardQueue.poll();
            if (forNode == null || backNode == null) {
                break;
            }

            if(forNode.getDist() + backNode.getDist() >= shortestDistance){
                break;
            }

            Node currNode = forNode.getNode();
            if (!currNode.getVisited()) {
                nodesVisited++;
                currNode.setVisited(true);

                //Go through all paths
                for(Path path : currNode.getOutgoingPaths()){
                    double newDistance = currNode.getDistance() + path.getWeight();

                    if (newDistance < path.getDestination().getDistance()) {
                        //add predecessor for the node
                        path.getDestination().setPredecessor(currNode);
                        path.getDestination().setDistance(newDistance);

                        if(path.getDestination().getVisited2()){
                            //newDistance + path.getDestination().getDistance2()
                            if(path.getDestination().getDistance2() + path.getDestination().getDistance() < shortestDistance){
                                shortestDistance = path.getDestination().getDistance2() + path.getDestination().getDistance();
                                midNode = path.getDestination();
                            }
                        }

                        //add to priority queue
                        nodesScanned++;
                        forwardQueue.add(new NodeWrapperImpl(path.getDestination(), path.getDestination().getDistance()));
                    }
                }
            }

            currNode = backNode.getNode();
            if (!currNode.getVisited2()) {
                nodesVisited++;
                currNode.setVisited2(true);

                //Go through all incoming paths
                for(Path path : currNode.getIncomingPaths()){

                    double newDistance = currNode.getDistance2() + path.getWeight();

                    if (newDistance < path.getSource().getDistance2()) {
                        //add predecessor for the node
                        path.getSource().setPredecessor2(currNode);
                        path.getSource().setDistance2(newDistance);

                        if(path.getSource().getVisited()){
                            //newDistance + path.getDestination().getDistance()
                            if(path.getSource().getDistance2() + path.getSource().getDistance() < shortestDistance){
                                shortestDistance = path.getSource().getDistance2() + path.getSource().getDistance();
                                midNode = path.getSource();
                            }
                        }

                        //add to priority queue
                        nodesScanned++;
                        backwardQueue.add(new NodeWrapperImpl(path.getSource(), path.getSource().getDistance2()));
                    }




                }
            }
        }

        //line up predecessors to forward
        if(midNode != null){
            while(midNode != destination) {
                Node nextNode = midNode.getPredecessor2();
                nextNode.setPredecessor(midNode);
                midNode = nextNode;
            }
        }

        /*
        Node temp;
        while(preNode != destination && preNode != null){
            temp = nextNode.getPredecessor();
            nextNode.setPredecessor(preNode);
            preNode = nextNode;
            nextNode = temp;
        }
         */


        //System.out.println("Bidirectional Dijkstra visited " + visitCount + " nodes");
        //System.out.println("Bidirectional Dijkstra took " + (finish - start) + " ms");
    }

    private void clearDistanceAndPredecessor(){
        Map<Long, Node> nodeMap = graph.getNodeMap();
        nodeMap.values().forEach(node -> {
           node.setDistance(Double.MAX_VALUE);
           node.setDistance2(Double.MAX_VALUE);
           node.setPredecessor(null);
           node.setPredecessor2(null);
           node.setVisited(false);
           node.setVisited2(false);
        });
    }

}


