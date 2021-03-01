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
    //private Map<Long, Long> predecessor;
    //private Map<Long, Double> distance;
    //private Set<Node> visited;
    private Long source = null;

    public DijkstraImpl(Graph graph){
        this.graph = graph;
        priorityQueue = new PriorityQueue<NodeWrapper>();
        //predecessor = new HashMap<Long, Long>();
        //distance = new HashMap<Long, Double>();
        //visited = new HashSet<Node>();

    }

    @Override
    public List<Long> getShortestPath(Long source, Long destination) {
        //if it is another source, or first run. Recalculate shortest path with dijkstra.
        if(true){ //could be optimized to only run if source and destination have changed since last run
            clearDistanceAndPredecessor();
            //visited.clear();
            priorityQueue.clear();
            runDijkstra(source, destination);
        }
        Map<Long, Node> nodeMap = graph.getNodeMap();
        List<Long> result = new ArrayList<Long>();
        Long currId = destination;
        while(!currId.equals(source)){
            result.add(currId);
            currId = nodeMap.get(currId).getPredecessor();
            if(currId == null){
                //return null if impossible
                return null;
            }
        }
        result.add(source);
        Collections.reverse(result);
        return result;
    }

    private void runDijkstra(Long source, Long destination){
        //measureable values
        int visitcount = 0;
        long start = System.currentTimeMillis();

        //add source to priority queue with distance 0
        Node firstNode = graph.getNodeMap().get(source);
        firstNode.setDistance(0);
        firstNode.setPredecessor(source);
        priorityQueue.add(new NodeWrapperImpl(firstNode, firstNode.getDistance()));

        while (true){
            NodeWrapper currNode = priorityQueue.poll();
            if(currNode == null || currNode.getNode().getId() == destination){
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
                        path.getDestination().setPredecessor(currNode.getNode().getId());
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

