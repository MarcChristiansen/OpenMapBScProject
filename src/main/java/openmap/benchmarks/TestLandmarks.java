package openmap.benchmarks;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.gui.LandmarkSelectionUtility;
import openmap.gui.PathFinderSelectionUtility;
import openmap.parsing.json.DiskUtility;
import openmap.utility.ConsoleUtils;

import java.io.IOException;
import java.util.*;

/**
 * Test class related to testing what the optimal choice of landmarks numbers and subset for algorithms are.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-04-2021
 */
public class TestLandmarks {

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

        LandmarkSelectionUtility lfsu = new LandmarkSelectionUtility(graph);
        PathFinderSelectionUtility pfsu = new PathFinderSelectionUtility(graph);
        String[] LandmarkStrings = lfsu.getLandmarkSelectionStrings();
        String[] PathfinderStrings = new String[3];
        PathfinderStrings[0] = "A*";
        PathfinderStrings[1] = "Landmark";
        PathfinderStrings[2] = "Landmark bi dir";

        if(target > -1){
            LandmarkStrings = new String[0];
            LandmarkStrings[0] = lfsu.getLandmarkSelectionStrings()[0];
            LandmarkStrings[1] = lfsu.getLandmarkSelectionStrings()[target];
        }

        Object[] values = graph.getNodeMap().values().toArray();
        Random random = new Random(12315341231L);

        Map<String, List<Node>> shortestPathMap = new HashMap<>();
        Map<String, List<Long>> executionTimesMap = new HashMap<>();
        Map<String, Integer> agreeWithDijkstraTimes = new HashMap<>();
        Map<String, Integer> pathNotExistTimes = new HashMap<>();





        for(int k = 1; k <= 32; k = k*2){ //number of landmarks, 1, 2, 4, 8, 16, 32
            for(String ls : LandmarkStrings){
                lfsu.getLandmarkSelector(ls).findLandmarks(k);
                for(int i = 1; i <= 8; i = i*2){ //subset size for landmark pathfinder
                    if(i > k){
                        break; //break if subset is bigger than landmark count
                    }
                    //reset execution times map
                    for(String s : PathfinderStrings){
                        executionTimesMap.put(s, new ArrayList<Long>());
                        pfsu.getPathFinder(s).SetLandmarkSubsetSize(i);
                    }

                    for(int j = 0; j < repetitions; j++){
                        shortestPathMap.clear();
                        Node source = (Node)values[random.nextInt(values.length)];
                        Node destination = (Node)values[random.nextInt(values.length)];

                        //get shortest path and save execution time
                        for(String ps : PathfinderStrings){
                            shortestPathMap.put(ps, pfsu.getPathFinder(ps).getShortestPath(source, destination));
                            executionTimesMap.get(ps).add(pfsu.getPathFinder(ps).getLastExecutionTime());
                        }

                        //compare path with dijkstra
                        for(String ps : PathfinderStrings){
                            List<Node> shortestPath = shortestPathMap.getOrDefault(ps, null);
                            List<Node> spDijkstra = shortestPathMap.getOrDefault("A*", null);

                            if((spDijkstra == null && shortestPath == null) || (spDijkstra != null && spDijkstra.equals(shortestPath))){
                                //update counter by 1
                                agreeWithDijkstraTimes.put(ps, agreeWithDijkstraTimes.getOrDefault(ps, 0)+1);
                            }
                            else{
                                System.out.println(source.getId());
                                System.out.println(destination.getId());
                            }
                            //check if path does not exist
                            if(shortestPath == null){
                                pathNotExistTimes.put(ps, pathNotExistTimes.getOrDefault(ps, 0)+1);
                            }
                        }
                    }

                    System.out.println(ls + " Landmarks count: " + k + " Subset size: " + i);
                    for(String ps : PathfinderStrings){
                        //System.out.println("times " + ps + " agreed with Dijkstra: " + agreeWithDijkstraTimes.getOrDefault(ps, 0));
                        //System.out.println("times " + ps + " found no path: " + pathNotExistTimes.getOrDefault(ps, 0));
                        long avgExecutionTime = 0;
                        for(Long l : executionTimesMap.get(ps)){
                            avgExecutionTime = avgExecutionTime+l;
                        }
                        avgExecutionTime = avgExecutionTime/executionTimesMap.get(ps).size();
                        System.out.println("Average execution time for " + ps +": " + avgExecutionTime);
                    }

                }
            }

        }
    }
}
