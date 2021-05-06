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
 * A funny Implementation of a bidirectional Dijkstra algorithm that really does not do what is intended
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class DijkstraBiDirVeryWrongImpl extends AbstractPathfinder {

    private PriorityQueue<NodeWrapper> forwardQueue;
    private PriorityQueue<NodeWrapper> backwardQueue;
    private double shortestDistance = Double.MAX_VALUE;
    private Node midNode = null;
    //private Map<Long, Long> predecessor;
    //private Map<Long, Double> distance;
    //private Set<Node> visited;
    private Long source = null;
    private long executionTime;

    public DijkstraBiDirVeryWrongImpl(Graph graph){
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
        nodesVisited = 0;
        nodesScanned = 0;
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
        return result;
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

    private void runBiDir(Node source, Node destination) {
        //measureable values
        long start = System.currentTimeMillis();

        boolean done = false;

        //add source to forward queue with distance 0
        source.setDistance(0);
        source.setPredecessor(source);
        forwardQueue.add(new NodeWrapperImpl(source, source.getDistance()));

        //add destination to backward queue with distance 0
        destination.setDistance(0);
        destination.setPredecessor2(destination);
        backwardQueue.add(new NodeWrapperImpl(destination, destination.getDistance()));

        /*
        Node preNode = null;
        Node nextNode = null;
         */

        while (true) {
            //forwards dijkstra
            NodeWrapper forNode = forwardQueue.poll();
            if (forNode == null) {
                System.out.println(done + "for");
                break;
            }

            //if already seen by backwards pass
            if(forNode.getNode().getPredecessor2() != null){
                System.out.println("checkNode " + forNode.getNode());
                //if new shortest path update variables
                if(shortestDistance >= forNode.getNode().getDistance2() + forNode.getNode().getDistance()){
                    shortestDistance = forNode.getNode().getDistance2() + forNode.getNode().getDistance();
                    midNode = forNode.getNode();
                    System.out.println("new midNode for " + midNode);
                }
                //otherwise end, found shortest path
                else {
                    System.out.println(source + " s");
                    System.out.println(destination + " d");
                    System.out.println("hello for");
                    System.out.println(midNode + " mid");
                    System.out.println(midNode.getPredecessor() + " pred1");
                    System.out.println(midNode.getPredecessor2() + " pred2");
                    System.out.println(shortestDistance);
                    break; //found midNode
                }
            }

            if (!forNode.getNode().getVisited() || forNode.getNode().getPredecessor() == null) {
                nodesVisited++;
                forNode.getNode().setVisited(true);

                //Go through all paths
                for(Path path : forNode.getNode().getOutgoingPaths()){
                    double newDistance = forNode.getNode().getDistance() + path.getWeight();

                    if(path.getDestination().getPredecessor() == null){
                        path.getDestination().setPredecessor(forNode.getNode());
                    }

                    if (newDistance < path.getDestination().getDistance()) {
                        //add predecessor for the node
                        path.getDestination().setPredecessor(forNode.getNode());
                        path.getDestination().setDistance(newDistance);

                        //add to priority queue
                        nodesScanned++;
                        forwardQueue.add(new NodeWrapperImpl(path.getDestination(), path.getDestination().getDistance()));
                    }


                }
            }

            NodeWrapper backNode = backwardQueue.poll();
            if (backNode == null) {
                System.out.println(done + " back");
                break;
            }

            //if already seen by forwards pass
            //System.out.println(backNode.getNode() + " node");
            //System.out.println(backNode.getNode().getPredecessor() + "pred1");
            //System.out.println(backNode.getNode().getPredecessor2() + "pred2");
            if(backNode.getNode().getPredecessor() != null){
                System.out.println("checkNode " + backNode.getNode());
                System.out.println(backNode.getNode().getDistance2());
                System.out.println(backNode.getNode().getDistance());
                //if new shortest path update variables
                if(shortestDistance >= backNode.getNode().getDistance2() + backNode.getNode().getDistance()){
                    shortestDistance = backNode.getNode().getDistance2() + backNode.getNode().getDistance();
                    midNode = backNode.getNode();
                    System.out.println("new midNode back " + midNode);
                }
                //otherwise end, found shortest path
                else {
                    System.out.println(source + " s");
                    System.out.println(destination + " d");
                    System.out.println("hello back");
                    System.out.println(midNode + " mid");
                    System.out.println(midNode.getPredecessor() + " pred1");
                    System.out.println(midNode.getPredecessor2() + " pred2");
                    System.out.println(shortestDistance);
                    break; //found midNode
                }
            }

            if (!backNode.getNode().getVisited() || backNode.getNode().getPredecessor2() == null) {
                nodesVisited++;
                backNode.getNode().setVisited(true);

                //Go through all incoming paths
                for(Path path : backNode.getNode().getIncomingPaths()){

                    double newDistance = backNode.getNode().getDistance2() + path.getWeight();

                    if(path.getSource().getPredecessor2() == null){
                        path.getSource().setPredecessor2(backNode.getNode());
                    }

                    if (newDistance < path.getSource().getDistance2()) {
                        //add predecessor for the node
                        path.getSource().setPredecessor2(backNode.getNode());
                        path.getSource().setDistance2(newDistance);


                    }

                    //add to priority queue
                    nodesScanned++;
                    backwardQueue.add(new NodeWrapperImpl(path.getSource(), path.getSource().getDistance()));
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

        long finish = System.currentTimeMillis();
        this.executionTime = finish - start;
        //System.out.println("Dijkstra visited " + visitCount + " nodes");
        //System.out.println("Dijkstra took " + (finish - start) + " ms");
    }



    private void runDijkstraold(Node source, Node destination){
        //measureable values
        int visitcount = 0;
        long start = System.currentTimeMillis();

        //add source to priority queue with distance 0
        Node firstNode = source;
        firstNode.setDistance(0);
        firstNode.setPredecessor(firstNode);
        forwardQueue.add(new NodeWrapperImpl(firstNode, firstNode.getDistance()));

        while (true){
            NodeWrapper currNode = forwardQueue.poll();
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
                    forwardQueue.add(new NodeWrapperImpl(path.getDestination(), path.getDestination().getDistance()));
                });
            }
        }
        long finish = System.currentTimeMillis();
        System.out.println("Dijkstra visited " + visitcount + " nodes");
        System.out.println("Dijkstra took " + (finish - start) + " ms");
    }

    private void clearDistanceAndPredecessor(){
        Map<Long, Node> nodeMap = graph.getNodeMap();
        nodeMap.values().forEach(node -> {
           node.setDistance(Double.MAX_VALUE);
           node.setDistance2(Double.MAX_VALUE);
           node.setPredecessor(null);
           node.setPredecessor2(null);
           node.setVisited(false);
        });
    }

}


