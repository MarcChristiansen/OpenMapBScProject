package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.framework.NodeWrapper;
import openmap.framework.PathFinder;

import java.util.*;

public class DijkstraBiDirImpl implements PathFinder {

    private Graph graph;
    private PriorityQueue<NodeWrapper> forwardQueue;
    private PriorityQueue<NodeWrapper> backwardQueue;
    private int visitCount;
    private double shortestDistance = Double.MAX_VALUE;
    private Node midNode = null;
    //private Map<Long, Long> predecessor;
    //private Map<Long, Double> distance;
    //private Set<Node> visited;
    private Long source = null;
    private long executionTime;

    public DijkstraBiDirImpl(Graph graph){
        this.graph = graph;
        this.executionTime = 0;
        forwardQueue = new PriorityQueue<NodeWrapper>();
        backwardQueue = new PriorityQueue<NodeWrapper>();
        visitCount = 0;
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
        visitCount = 0;
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
    public long getLastExecutionTime() {
        return executionTime;
    }

    private void runBiDir(Node source, Node destination) {
        //measureable values
        long start = System.currentTimeMillis();

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
                visitCount++;
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
                    }



                    //add to priority queue
                    forwardQueue.add(new NodeWrapperImpl(path.getDestination(), path.getDestination().getDistance()));
                }
            }

            currNode = backNode.getNode();
            if (!currNode.getVisited2()) {
                visitCount++;
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
                    }



                    //add to priority queue
                    backwardQueue.add(new NodeWrapperImpl(path.getSource(), path.getSource().getDistance2()));
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
        executionTime = finish - start;
        System.out.println("Dijkstra visited " + visitCount + " nodes");
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
           node.setVisited2(false);
        });
    }

}


