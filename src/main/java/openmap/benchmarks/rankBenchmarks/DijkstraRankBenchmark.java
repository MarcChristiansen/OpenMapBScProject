package openmap.benchmarks.rankBenchmarks;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.PathFinder;
import openmap.gui.PathFinderSelectionUtility;
import openmap.parsing.json.DiskUtility;
import openmap.utility.FileWriter;
import openmap.utility.LatexUtility;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DijkstraRankBenchmark {

    public static int dijkstraRankIterations = 5 ;
    public static int maxRank = 25;

    public static void main(String[] args) throws IOException {
        Graph graph = null;
        try { graph = DiskUtility.loadJsonGraph(args[0]); } //Load graph
        catch (Exception e){ e.printStackTrace(); }

        PathFinderSelectionUtility pfsu = new PathFinderSelectionUtility(graph);

        Random random = new Random(12315341231L);
        List<SingleDijkstraRank> testIterRanks = getRanks(graph, random);

        int actualRank = Math.min(maxRank, testIterRanks.get(0).getRanks().size());

        long[][] accRes = new long[pfsu.getRelevantPathfinderNames().size()][actualRank];

        System.out.println(testIterRanks.get(0).getStart().getId());
        System.out.println(testIterRanks.get(0).getRanks().get(0).getId());


        for (int i = 0; i < pfsu.getRelevantPathfinderNames().size(); i++) {
        PathFinder pf = pfsu.getRelevantPathFinder().get(i);
            for (SingleDijkstraRank sdr:testIterRanks) {
                for (int j = 0; j < actualRank; j++) {
                    Node target = sdr.getRanks().get(j);
                    pf.getShortestPath(sdr.getStart(), target);
                    accRes[i][j] += pf.getLastExecutionTime();
                }
            }
        }

        List<List<String>> tableRows = new ArrayList<>();

        //Add top row with rank numbers
        tableRows.add(new ArrayList<>());
        tableRows.get(0).add("Pathfinder");
        List<String> range = IntStream.rangeClosed(0, actualRank-1)
                .mapToObj(Integer::toString)
                .collect(Collectors.toList());
        tableRows.get(0).addAll(range);

        for (int i = 0; i < accRes.length; i++) {
            tableRows.add(new ArrayList<>());
            tableRows.get(i+1).add(pfsu.getRelevantPathfinderNames().get(i));
            for (int j = 0; j < accRes[0].length; j++) {
                tableRows.get(i+1).add(String.format(Locale.GERMANY,"%.2f", (double)accRes[i][j]/(double)dijkstraRankIterations));
            }
        }

        FileWriter.writeCSV(tableRows,  args[0] + "-dijkstraRank");
    }

    private static List<SingleDijkstraRank> getRanks(Graph graph, Random random) {
        List<SingleDijkstraRank> res = new ArrayList<>();
        DijkstraRankCreator dc = new DijkstraRankCreator(graph);
        Object[] values = graph.getNodeMap().values().toArray();

        //Generate dijkstra ranks
        for (int i = 0; i < dijkstraRankIterations; i++) {
            while(res.size() <= i){
                Node source = (Node)values[random.nextInt(values.length)];

                dc.getShortestPath(source, null);

                if(res.size() == 0 || dc.getNodeRanks().getRanks().size() >= res.get(0).getRanks().size()) {
                    res.add(dc.getNodeRanks()); //Only add if rank is at least as high as original
                }

            }
            System.out.println("Dijkstra rank source " + (i+1) + " generated");
        }

        System.out.println("Rank size min: " + res.get(0).getRanks().size());

        return res;
    }


}
