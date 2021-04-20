package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.NodeWrapper;
import openmap.framework.PathFinder;
import openmap.gui.NodeDrawingInfo;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Simple Dijkstra implementation using a nodeWrapper and storage in the nodes
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 25-02-2021
 */
public class DijkstraImpl implements PathFinder {

    private Graph graph;
    private PriorityQueue<NodeWrapper> priorityQueue;
    private long executionTime;

    public DijkstraImpl(Graph graph){
        this.graph = graph;
        this.executionTime = 0;
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
        runDijkstra(source, destination);

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

    private void runDijkstra(Node source, Node destination){
        //measureable values
        int visitcount = 0;
        long start = System.currentTimeMillis();

        //add source to priority queue with distance 0
        Node firstNode = source;
        firstNode.setDistance(0);
        firstNode.setPredecessor(firstNode);
        priorityQueue.add(new NodeWrapperImpl(firstNode, firstNode.getDistance()));

        while (true){
            NodeWrapper currNode = priorityQueue.poll();
            if(currNode == null || currNode.getNode().getId() == destination.getId()){
                break; //we obey whatever Gerth commands
            }

            if(!currNode.getNode().getVisited()){
                visitcount++;

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
                    }

                    //add to priority queue
                    priorityQueue.add(new NodeWrapperImpl(path.getDestination(), path.getDestination().getDistance()));
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
        });
    }

}


