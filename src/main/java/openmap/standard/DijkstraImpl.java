package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.NodeWrapper;
import openmap.framework.PathFinder;

import java.sql.SQLOutput;
import java.util.*;

public class DijkstraImpl implements PathFinder {

    private Graph graph;
    private PriorityQueue<NodeWrapper> priorityQueue;

    public DijkstraImpl(Graph graph){
        this.graph = graph;
        priorityQueue = new PriorityQueue<NodeWrapper>();
        //predecessor = new HashMap<Long, Long>();
        //distance = new HashMap<Long, Double>();
        //visited = new HashSet<Node>();

    }

    @Override
    public List<Node> getShortestPath(Node source, Node destination) {
        //if it is another source, or first run. Recalculate shortest path with dijkstra.
        if(true){ //could be optimized to only run if source and destination have changed since last run
            clearDistanceAndPredecessor();
            //visited.clear();
            priorityQueue.clear();
            runDijkstra(source, destination);
        }
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
                currNode.getNode().getPaths().forEach(path -> {
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
        System.out.println("Dijkstra visited " + visitcount + " nodes");
        System.out.println("Dijkstra took " + (finish - start) + " ms");
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


