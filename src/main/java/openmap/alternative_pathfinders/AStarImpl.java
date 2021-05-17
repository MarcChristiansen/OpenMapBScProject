package openmap.alternative_pathfinders;

import openmap.framework.*;
import openmap.gui.NodeDrawingInfo;
import openmap.standard.AbstractPathfinder;
import openmap.standard.NodeWrapperImpl;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of the standard A* algorithm
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class AStarImpl extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> priorityQueue;

    private Node currTarget;

    public AStarImpl(Graph graph){
        super(graph);
        priorityQueue = new PriorityQueue<NodeWrapper>();
    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {
        //Prepare for A* run
        clearDistanceAndPredecessor();
        priorityQueue.clear();

        //Initial setup
        nodesVisited = 0;
        nodesScanned = 0;
        long start = System.currentTimeMillis();
        List<Node> path = null;
        currTarget = destination;
        source.setDistance(0);
        priorityQueue.add(new NodeWrapperImpl(source, h(source)));

        NodeWrapper currNodeW = null;
        while (!priorityQueue.isEmpty()){
            currNodeW = priorityQueue.poll();
            nodesVisited++;

            if(currNodeW.getNode() == currTarget){
                path = retraceSteps(source);
                break;
            }

            if(!currNodeW.getNode().getVisited() ){
                currNodeW.getNode().setVisited(true);

                for (Path p: currNodeW.getNode().getOutgoingPaths()) {
                    double totalWeight = currNodeW.getNode().getDistance()+p.getWeight();
                    Node pathDest = p.getDestination();

                    //If the newly discovered node has not been handled before or we found a shorter path to it
                    if(totalWeight < pathDest.getDistance()) {
                        pathDest.setDistance(totalWeight);
                        pathDest.setPredecessor(currNodeW.getNode());

                        nodesScanned++;
                        priorityQueue.add(new NodeWrapperImpl(pathDest, totalWeight+h(pathDest)));
                    }
                }
            }
        }

        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
        //System.out.println("A* took " + (this.executionTime) + " ms");

        return path;
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

    private List<Node> retraceSteps(Node source){
        List<Node> res = new ArrayList<>();

        Node currNode = currTarget;
        while (currNode != source){
            res.add(currNode);
            currNode = currNode.getPredecessor();

            if(currNode == null){
                //return null if impossible
                return null;
            }
        }

        res.add(source);
        Collections.reverse(res);

        return res;
    }

    private double h(Node n){
        return distance(n, currTarget);
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

        });
    }
}
