package openmap.runnable;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.PathFinder;
import openmap.gui.PathFinderSelectionUtility;
import openmap.parsing.json.DiskUtility;
import openmap.utility.ConsoleUtils;

import java.io.IOException;
import java.util.*;

/**
 * Allows for testing one or all pathfinding algorithms against dijkstra
 *
 * Uses the PathFinderSelectionUtility to gain access to pathfinders.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-02-2021
 */
public class TestPathfinders {

    public static void main(String args[]) throws IOException {
        String path = "";
        int target = -1;
        int repetitions;

        if(args != null && args.length == 2){
            path = args[0];
            target = Integer.parseInt(args[1]);
        }
        else{
            path = ConsoleUtils.readLine(
                    "Enter json path : ");
            target = Integer.parseInt(ConsoleUtils.readLine(
                    "Enter target fun index... (-1) for all : "));
        }

        repetitions = Integer.parseInt(ConsoleUtils.readLine("Enter number of repetitions: "));


        Graph graph = null;
        try { graph = DiskUtility.loadJsonGraph(path); }
        catch (Exception e){ e.printStackTrace(); }

        PathFinderSelectionUtility pfsu = new PathFinderSelectionUtility(graph);
        String[] pathFinderStrings = pfsu.getPathFinderStrings();

        if(target > -1){
            pathFinderStrings = new String[2];
            pathFinderStrings[0] = pfsu.getPathFinderStrings()[0];
            pathFinderStrings[1] = pfsu.getPathFinderStrings()[target];
        }

        Object[] values = graph.getNodeMap().values().toArray();
        Random random = new Random(12315341231L);

        Map<String, List<Node>> shortestPathMap = new HashMap<>();
        Map<String, List<Long>> executionTimesMap = new HashMap<>();
        Map<String, Integer> agreeWithDijkstraTimes = new HashMap<>();
        Map<String, Integer> pathNotExistTimes = new HashMap<>();

        for(String s : pathFinderStrings){
            executionTimesMap.put(s, new ArrayList<Long>());
        }

        for(int i = 0; i < repetitions; i++){
            //get two nodes
            shortestPathMap.clear();
            Node source = (Node)values[random.nextInt(values.length)];
            Node destination = (Node)values[random.nextInt(values.length)];

            //get shortest path from every pathfinder
            for(String s : pathFinderStrings){
                shortestPathMap.put(s, pfsu.getPathFinder(s).getShortestPath(source, destination));
                executionTimesMap.get(s).add(pfsu.getPathFinder(s).getLastExecutionTime());
            }

            //check path with dijkstra
            for(String s : pathFinderStrings){
                List<Node> shortestPath = shortestPathMap.getOrDefault(s, null);
                List<Node> spDijkstra = shortestPathMap.getOrDefault("Dijkstra", null);

                if((spDijkstra == null && shortestPath == null) || (spDijkstra != null && spDijkstra.equals(shortestPath))){
                    //update counter by 1
                    agreeWithDijkstraTimes.put(s, agreeWithDijkstraTimes.getOrDefault(s, 0)+1);
                }
                else{
                    System.out.println(source.getId());
                    System.out.println(destination.getId());
                }
                //check if path does not exist
                if(shortestPath == null){
                    pathNotExistTimes.put(s, pathNotExistTimes.getOrDefault(s, 0)+1);
                }
            }
        }

        //print times agreed with dijkstra
        for(String s : pathFinderStrings){
            System.out.println("times " + s + " agreed with Dijkstra: " + agreeWithDijkstraTimes.getOrDefault(s, 0));
            System.out.println("times " + s + " found no path: " + pathNotExistTimes.getOrDefault(s, 0));
            long avgExecutionTime = 0;
            for(Long l : executionTimesMap.get(s)){
                avgExecutionTime = avgExecutionTime+l;
            }
            avgExecutionTime = avgExecutionTime/executionTimesMap.get(s).size();
            System.out.println("Average execution time for " + s +": " + avgExecutionTime);
        }

        //edges relaxed
        //edges giving shorter distances


    }
}
