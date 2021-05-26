package openmap.benchmarks;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.gui.LandmarkSelectionUtility;
import openmap.gui.PathFinderSelectionUtility;
import openmap.parsing.json.DiskUtility;
import openmap.utility.ConsoleUtils;
import openmap.utility.LatexUtility;

import java.io.*;
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
                    "Enter target landmark selector index... (-1) for all : "));
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
            LandmarkStrings = new String[2];
            LandmarkStrings[0] = lfsu.getLandmarkSelectionStrings()[0];
            LandmarkStrings[1] = lfsu.getLandmarkSelectionStrings()[target];
        }

        Object[] values = graph.getNodeMap().values().toArray();

        Map<String, List<Node>> shortestPathMap = new HashMap<>();
        Map<String, List<Long>> executionTimesMap = new HashMap<>();
        Map<String, Integer> agreeWithDijkstraTimes = new HashMap<>();
        Map<String, Integer> pathNotExistTimes = new HashMap<>();

        List<List<String>> dataLandmark = new ArrayList<>();
        List<String> header = new ArrayList<>();
        header.add("Landmark");
        header.add("Landmarks");
        header.add("Subset 1");
        header.add("Subset 2");
        header.add("Subset 4");
        header.add("Subset 8");
        header.add("Subset 16");
        dataLandmark.add(header);

        List<List<String>> dataLandmarkEff = new ArrayList<>();
        dataLandmarkEff.add(header);

        List<List<String>> dataLandmarkBiDir = new ArrayList<>();
        List<String> headerBi = new ArrayList<>();
        headerBi.add("Landmark bi Dir");
        headerBi.add("Landmarks");
        headerBi.add("Subset 1");
        headerBi.add("Subset 2");
        headerBi.add("Subset 4");
        headerBi.add("Subset 8");
        headerBi.add("Subset 16");
        dataLandmarkBiDir.add(headerBi);

        List<List<String>> dataLandmarkBiDirEff = new ArrayList<>();
        dataLandmarkBiDirEff.add(headerBi);

        List<String> rowLandmark = new ArrayList<>();
        List<String> rowLandmarkBiDir = new ArrayList<>();
        List<String> rowLandmarkEff = new ArrayList<>();
        List<String> rowLandmarkBiDirEff = new ArrayList<>();

        for(int k = 1; k <= 64; k = k*2){ //number of landmarks, 1, 2, 4, 8, 16, 32

            for(String ls : LandmarkStrings){
                rowLandmark = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));
                rowLandmarkBiDir = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));
                rowLandmark.set(0, ls);
                rowLandmark.set(1, k+"");
                rowLandmarkBiDir.set(0, ls);
                rowLandmarkBiDir.set(1, k+"");

                rowLandmarkEff = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));
                rowLandmarkBiDirEff = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));
                rowLandmarkEff.set(0, ls);
                rowLandmarkEff.set(1, k+"");
                rowLandmarkBiDirEff.set(0, ls);
                rowLandmarkBiDirEff.set(1, k+"");

                lfsu.getLandmarkSelector(ls).findLandmarks(k);
                int i0 = 0;
                for(int i = 1; i <= 16; i = i*2){ //subset size for landmark pathfinder
                    i0++;
                    Random random = new Random(12315341231L);
                    if(i > k){
                        break; //break if subset is bigger than landmark count
                    }
                    //reset execution times map
                    for(String s : PathfinderStrings){
                        executionTimesMap.put(s, new ArrayList<Long>());
                        pfsu.getPathFinder(s).SetLandmarkSubsetSize(i);
                    }

                    float accEffLandmark = 0;
                    float accEffLandmarkBidir = 0;

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

                                if(shortestPath != null){
                                    //add acc Eff
                                    if(ps.equals("Landmark")){
                                        accEffLandmark = accEffLandmark + (float)(shortestPath.size())/(float)(pfsu.getPathFinder(ps).getNodesVisited())*100;
                                    }
                                    else if(ps.equals("Landmark bi dir")){
                                        accEffLandmarkBidir = accEffLandmarkBidir + (float)(shortestPath.size())/(float)(pfsu.getPathFinder(ps).getNodesVisited())*100;
                                    }
                                }
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
                        if(ps.equals(PathfinderStrings[1])){
                            rowLandmark.set(i0 + 1, avgExecutionTime+" ms");
                            rowLandmarkEff.set(i0 + 1, (accEffLandmark/repetitions)+"%");
                            System.out.println("Average efficiency for " + ps +": " + (accEffLandmark/repetitions)+"%");
                        }
                        else if(ps.equals(PathfinderStrings[2])){
                            rowLandmarkBiDir.set(i0 + 1, avgExecutionTime+" ms");
                            rowLandmarkBiDirEff.set(i0 + 1, (accEffLandmarkBidir/repetitions)+"%");
                            System.out.println("Average efficiency for " + ps +": " + (accEffLandmarkBidir/repetitions)+"%");
                        }
                        System.out.println("Average execution time for " + ps +": " + avgExecutionTime);
                    }

                }
                dataLandmark.add(rowLandmark);
                dataLandmarkBiDir.add(rowLandmarkBiDir);
                dataLandmarkEff.add(rowLandmarkEff);
                dataLandmarkBiDirEff.add(rowLandmarkBiDirEff);
            }

        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("TestLandmarksLandmark.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(dataLandmark));
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("TestLandmarksLandmarkBiDir.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(dataLandmarkBiDir));
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("TestLandmarksLandmarkEff.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(dataLandmarkEff));
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("TestLandmarksLandmarkBiDirEff.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(dataLandmarkBiDirEff));
        }

    }
}
