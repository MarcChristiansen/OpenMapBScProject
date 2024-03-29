package openmap.alternative_pathfinders;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.PathFinder;
import openmap.gui.NodeDrawingInfo;
import openmap.standard.AbstractPathfinder;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Wrong Dijkstra implementation that uses mutable state in the priority queue
 *
 * Mostly does not cause issues but might differ in some cases, which is a problem (therefore it is wrong)
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 25-02-2021
 */
public class DijkstraWrongImpl extends AbstractPathfinder {

    private PriorityQueue<Node> priorityQueue;
    private Set<Node> visited;
    private Node source = null;

    public DijkstraWrongImpl(Graph graph){
        super(graph);
        priorityQueue = new PriorityQueue<Node>();
        //predecessor = new HashMap<Long, Long>();
        //distance = new HashMap<Long, Double>();
        visited = new HashSet<Node>();

    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {
        clearDistanceAndPredecessor();
        visited.clear();
        priorityQueue.clear();
        nodesVisited = 0;
        nodesScanned = 0;
        runDijkstra(source, destination);

        List<Node> result = new ArrayList<>();
        Node currNode = destination;
        while(!(currNode.getId() == (source.getId()))){
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

    private void runDijkstra(Node source, Node destination){
        //measureable values
        long start = System.currentTimeMillis();

        //add source to priority queue with distance 0
        Node firstNode = source;
        firstNode.setDistance(0);
        priorityQueue.add(firstNode);

        int nodeCount = graph.getNodeMap().size();

        boolean finished = false;

        while (!finished){
            Node currNode = priorityQueue.poll();

            if(currNode == null || currNode.getId() == destination.getId()){
                finished = true;
            }
            else if(!visited.contains(currNode)){
                nodesVisited++;
                //Add node to visited
                visited.add(currNode);

                //Go through all paths
                currNode.getOutgoingPaths().forEach(path -> {
                    double newDistance = currNode.getDistance() + path.getWeight();

                    //check if new distance is lower
                    if(newDistance < path.getDestination().getDistance()) {
                        path.getDestination().setDistance(newDistance);
                        //add predecessor for the node
                        path.getDestination().setPredecessor(currNode);

                        //add to priority queue
                        nodesScanned++;
                        priorityQueue.add(path.getDestination());
                    }


                });
            }
        }
        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
        //System.out.println("Dijkstra wrong visited " + visitcount + " nodes");
        //System.out.println("Dijkstra wrong took " + (finish - start) + " ms");
    }

    private void clearDistanceAndPredecessor(){
        Map<Long, Node> nodeMap = graph.getNodeMap();
        nodeMap.values().forEach(node -> {
           node.setDistance(Double.MAX_VALUE);
           node.setPredecessor(null);
        });
    }

}


