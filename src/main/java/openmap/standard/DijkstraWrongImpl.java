package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.PathFinder;

import java.util.*;

public class DijkstraWrongImpl implements PathFinder {

    private Graph graph;
    private PriorityQueue<Node> priorityQueue;
    //private Map<Long, Long> predecessor;
    //private Map<Long, Double> distance;
    private Set<Node> visited;
    private Long source = null;

    public DijkstraWrongImpl(Graph graph){
        this.graph = graph;
        priorityQueue = new PriorityQueue<Node>();
        //predecessor = new HashMap<Long, Long>();
        //distance = new HashMap<Long, Double>();
        visited = new HashSet<Node>();

    }

    @Override
    public List<Long> getShortestPath(Long source, Long destination) {
        //if it is another source, or first run. Recalculate shortest path with dijkstra.
        if(this.source == null || this.source != source){
            clearDistanceAndPredecessor();
            visited.clear();
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
        priorityQueue.add(firstNode);

        int nodeCount = graph.getNodeMap().size();

        boolean finished = false;

        while (!finished){
            Node currNode = priorityQueue.poll();

            if(currNode == null || currNode.getId() == destination){
                finished = true;
            }
            else if(!visited.contains(currNode)){
                visitcount++;
                //Add node to visited
                visited.add(currNode);

                //Go through all paths
                currNode.getPaths().forEach(path -> {
                    double newDistance = currNode.getDistance() + path.getWeight();

                    //check if new distance is lower
                    if(newDistance < path.getDestination().getDistance()) {
                        path.getDestination().setDistance(newDistance);
                        //add predecessor for the node
                        path.getDestination().setPredecessor(currNode.getId());
                    }

                    //add to priority queue
                    priorityQueue.add(path.getDestination());
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
        });
    }

}


