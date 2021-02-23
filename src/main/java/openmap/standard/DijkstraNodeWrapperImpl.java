package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.NodeWrapper;
import openmap.framework.PathFinder;

import java.util.*;

public class DijkstraNodeWrapperImpl implements PathFinder {

    private Graph graph;
    private PriorityQueue<NodeWrapper> priorityQueue;
    private Map<Long, Long> predecessor;
    private Map<Long, Double> distance;
    private Set<Node> visited;
    private Long source = null;

    public DijkstraNodeWrapperImpl(Graph graph){
        this.graph = graph;
        priorityQueue = new PriorityQueue<NodeWrapper>();
        predecessor = new HashMap<Long, Long>();
        distance = new HashMap<Long, Double>();
        visited = new HashSet<Node>();

    }

    @Override
    public List<Long> getShortestPath(Long source, Long destination) {
        //if it is another source, or first run. Recalculate shortest path with dijkstra.
        if(this.source == null || this.source != source){
            predecessor.clear();
            distance.clear();
            visited.clear();
            priorityQueue.clear();
            runDijkstra(source, destination);
        }
        List<Long> result = new ArrayList<Long>();
        Long currId = destination;
        while(!currId.equals(source)){
            result.add(currId);
            currId = predecessor.get(currId);
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
        priorityQueue.add(new NodeWrapperImpl(graph.getNodeMap().getOrDefault(source, null), 0.0));
        distance.put(source, 0.0);

        int nodeCount = graph.getNodeMap().size();

        boolean finished = false;

        while (!finished){
            NodeWrapper currNode = priorityQueue.poll();

            if(currNode == null || currNode.getNode().getId() == destination){
                finished = true;
            }
            else if(!visited.contains(currNode.getNode())){
                visitcount++;
                //Add node to visited
                visited.add(currNode.getNode());

                //Go through all paths
                currNode.getNode().getPaths().forEach(path -> {
                    double newDistance = currNode.getDist() + path.getWeight();

                    //check if new distance is lower
                    if(newDistance < distance.getOrDefault(path.getDestinationId(), Double.MAX_VALUE)) {
                        distance.put(path.getDestinationId(), newDistance);
                        //add predecessor for the node
                        predecessor.put(path.getDestinationId(), currNode.getNode().getId());
                    }

                    //add to priority queue
                    priorityQueue.add(new NodeWrapperImpl(path.getDestination(), distance.getOrDefault(path.getDestinationId(), Double.MAX_VALUE)));
                });
            }
        }
        long finish = System.currentTimeMillis();
        System.out.println("Dijkstra visited " + visitcount + " nodes");
        System.out.println("Dijkstra took " + (finish - start) + " ms");
    }


}


