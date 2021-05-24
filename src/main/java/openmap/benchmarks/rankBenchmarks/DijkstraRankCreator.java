package openmap.benchmarks.rankBenchmarks;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.NodeWrapper;
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
public class DijkstraRankCreator extends AbstractPathfinder {

    public static int[] pathfinderPfsuRefs = {0, 2, 3, 5, 6, 7};
    Node startNode;
    List<Node> nodeRanks;

    private PriorityQueue<NodeWrapper> priorityQueue;

    public DijkstraRankCreator(Graph graph){
        super(graph);
        priorityQueue = new PriorityQueue<NodeWrapper>();
    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {
        //We always recalculate. Might not be efficient of same source but helps with compatibility for multiple algos
        clearDistanceAndPredecessor();
        //visited.clear();
        priorityQueue.clear();
        runDijkstra(source, destination);


        return null; //No need to return a result for this one...
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

    public boolean powerOfTwo(int n)
    {
        return (int)(Math.ceil((Math.log(n) / Math.log(2))))
                == (int)(Math.floor(((Math.log(n) / Math.log(2)))));
    }

    private void runDijkstra(Node source, Node destination){
        nodeRanks = new ArrayList<>();
        startNode = source;

        //measureable values
        nodesVisited = 0;
        nodesScanned = 0;
        long start = System.currentTimeMillis();

        //add source to priority queue with distance 0
        Node firstNode = source;
        firstNode.setDistance(0);
        firstNode.setPredecessor(firstNode);
        priorityQueue.add(new NodeWrapperImpl(firstNode, firstNode.getDistance()));

        while (!priorityQueue.isEmpty()){
            NodeWrapper currNode = priorityQueue.poll();

            if(!currNode.getNode().getVisited()){
                nodesVisited++; //update visited nodes

                if(powerOfTwo(nodesVisited)){
                    nodeRanks.add(currNode.getNode()); //Add node to rank list
                }

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
                        nodesScanned++; //update scanned nodes
                        priorityQueue.add(new NodeWrapperImpl(path.getDestination(), path.getDestination().getDistance()));
                    }


                });
            }
        }
        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
    }

    private void clearDistanceAndPredecessor(){
        Map<Long, Node> nodeMap = graph.getNodeMap();
        nodeMap.values().forEach(node -> {
           node.setDistance(Double.MAX_VALUE);
           node.setPredecessor(null);
           node.setVisited(false);
        });
    }

    public SingleDijkstraRank getNodeRanks() {
        return new SingleDijkstraRank(startNode, nodeRanks);
    }
}


