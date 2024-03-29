package openmap.alternative_pathfinders;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.NodeWrapper;
import openmap.framework.PathFinder;
import openmap.gui.NodeDrawingInfo;
import openmap.standard.AbstractPathfinder;
import openmap.standard.NodeWrapperImpl;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

/**
 * Simple Dijkstra implementation using a nodeWrapper and storage in the nodes
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 25-02-2021
 */
public class LandmarkDijkstraImplForward extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> priorityQueue;

    public LandmarkDijkstraImplForward(Graph graph){
        super(graph);
        priorityQueue = new PriorityQueue<NodeWrapper>();
        //predecessor = new HashMap<Long, Long>();
        //distance = new HashMap<Long, Double>();
        //visited = new HashSet<Node>();

    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {
        //We always recalculate. Might not be efficient of same source but helps with compatibility for multiple algos
        clearDistanceAndPredecessor();
        //visited.clear();
        priorityQueue.clear();
        nodesVisited = 0;
        nodesScanned = 0;
        runDijkstra(source); //Runs both an incoming and a reverse one...

        List<Node> result = new ArrayList<>();

        Node currNode = destination;
        while(!(currNode.getId() == source.getId())){
            result.add(currNode);
            currNode = currNode.getPredecessor();
            if(currNode == null){
                //return null if impossible
                return null;
            }
        }
        result.add(source);
        Collections.reverse(result);
        return result;
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
        return null;
    }

    @Override
    public List<Integer> getLandmarksUsedFrom() {
        return null;
    }

    @Override
    public void SetLandmarkSubsetSize(int i) {

    }

    private void runDijkstra(Node source){
        //measureable values
        long start = System.currentTimeMillis();

        //add source to priority queue with distance 0
        Node firstNode = source;
        firstNode.setDistance(0);
        firstNode.setPredecessor(firstNode);
        priorityQueue.add(new NodeWrapperImpl(firstNode, firstNode.getDistance()));

        while (true){
            NodeWrapper currNode = priorityQueue.poll();
            if(currNode == null){ //if queue is empty
                break;
            }

            if(!currNode.getNode().getVisited()){
                nodesVisited++;

                //give first node predecessor
                currNode.getNode().setVisited(true);
                //Go through all paths
                currNode.getNode().getOutgoingPaths().forEach(path -> {
                    double newDistance = currNode.getNode().getDistance() + path.getWeight();

                    //check if new distance is lower
                    if(newDistance < path.getDestination().getDistance()) {
                        path.getDestination().setDistance(newDistance);
                        //add predecessor for the node
                        path.getDestination().setPredecessor(currNode.getNode());

                        //add to priority queue
                        nodesScanned++;
                        priorityQueue.add(new NodeWrapperImpl(path.getDestination(), path.getDestination().getDistance()));
                    }
                });
            }
        }

        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
        //System.out.println("Dijkstra visited " + visitcount + " nodes");
        //System.out.println("Dijkstra took " + (finish - start) + " ms");
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


