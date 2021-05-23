package openmap.benchmarks;

import openmap.framework.*;
import openmap.gui.PathFinderSelectionUtility;
import openmap.parsing.json.DiskUtility;
import openmap.utility.FileWriter;
import openmap.utility.LatexUtility;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BenchmarkPathfindersSimple {

    public static int[] pathfinderPfsuRefs = {0, 2, 3, 5, 6, 7};
    public static double[] avgNumList;
    public static double[] avgEffList;
    public static List<List<String>> tableRowsRun;
    public static List<List<String>> tableRowsEff;

    public static void main(String[] args) throws IOException {
        tableRowsRun = new ArrayList<>();
        tableRowsEff = new ArrayList<>();

        avgNumList = new double[pathfinderPfsuRefs.length];
        avgEffList = new double[pathfinderPfsuRefs.length];

        PathFinderSelectionUtility pfsu = new PathFinderSelectionUtility(null); //Not really used, only for names...

        ArrayList<String> tempTop = new ArrayList<>();

        tempTop.add("kort");

        for (int i: pathfinderPfsuRefs) {
            tempTop.add(pfsu.getPathFinderStrings()[i]);
        }

        tableRowsRun.add(tempTop);
        tableRowsEff.add(tempTop);


        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            BenchmarkSpecificMap(args[i]);
        }

        List<String> avgList = Arrays.stream(avgNumList).map(d -> d/(double)(args.length))
                                .mapToObj(d -> String.format(Locale.GERMANY,"%.2f", d))
                                .collect(Collectors.toList());
        List<String> finalAvgList = new ArrayList<>();
        finalAvgList.add("Gennemsnit");
        finalAvgList.addAll(avgList);


        List<String> effAvgList = Arrays.stream(avgEffList).map(d -> d/(double)(args.length))
                .mapToObj(d -> String.format(Locale.GERMANY,"%.2f", d))
                .collect(Collectors.toList());
        List<String> finalAvgEffList = new ArrayList<>();
        finalAvgEffList.add("Gennemsnit");
        finalAvgEffList.addAll(effAvgList);


        tableRowsRun.add(finalAvgList);

        System.out.println("\n% Latex full table %");

        System.out.println(LatexUtility.generateStandardTable(tableRowsRun));
        System.out.println();
        System.out.println(LatexUtility.generateStandardTable(tableRowsEff));


        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("simplePathfinderBenchmarks.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(tableRowsRun));
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("simplePathfinderEffBenchmarks.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(tableRowsEff));
        }
    }

    public static void BenchmarkSpecificMap(String path){

        int testAmount = 250; //Number of tests to run



        Graph graph = null;
        try { graph = DiskUtility.loadJsonGraph(path); }
        catch (Exception e){ e.printStackTrace(); }

        Object[] values = graph.getNodeMap().values().toArray();

        PathFinderSelectionUtility pfsu = new PathFinderSelectionUtility(graph);




        long[] accTimes = new long[pathfinderPfsuRefs.length];
        double[] accEff = new double[pathfinderPfsuRefs.length];

        List<List<String>> resultsRawTime = new ArrayList<>();
        List<List<String>> resultsRawEff = new ArrayList<>();

        for (int i = 0; i < pathfinderPfsuRefs.length; i++) {
            String s = pfsu.getPathFinderStrings()[pathfinderPfsuRefs[i]];
            List<String> pathfinderRawTimeStringList = new ArrayList<>();
            List<String> pathfinderRawEffStringList = new ArrayList<>();

            pathfinderRawTimeStringList.add(s);
            pathfinderRawEffStringList.add(s);

            resultsRawTime.add(pathfinderRawTimeStringList);
            resultsRawEff.add(pathfinderRawEffStringList);
        }

        System.out.println();
        System.out.println(path);
        System.out.println();

        for (int i = 0; i < pathfinderPfsuRefs.length; i++) {
            Random random = new Random(12315341231L);
            PathFinder p = pfsu.getPathFinder(pfsu.getPathFinderStrings()[pathfinderPfsuRefs[i]]);

            for (int j = 0; j < testAmount; j++) {
                Node source = (Node)values[random.nextInt(values.length)];
                Node destination = (Node)values[random.nextInt(values.length)];

                while(source == destination || pfsu.getPathFinder(pfsu.getPathFinderStrings()[pathfinderPfsuRefs[pathfinderPfsuRefs.length-1]]).getShortestPath(source, destination) == null)
                {
                    source = (Node)values[random.nextInt(values.length)];
                    destination = (Node)values[random.nextInt(values.length)];
                }

                List<Node> sPath = p.getShortestPath(source, destination);
                long time = p.getLastExecutionTime();

                int length = 0;
                if(sPath != null)
                    length = sPath.size();

                double eff = 0;

                if(p.getNodesVisited() > 0) {
                    eff = (double) (length) / (double) (p.getNodesVisited()) * 100.0;
                }

                resultsRawTime.get(i).add(Long.toString(time));
                resultsRawEff.get(i).add(String.format(Locale.GERMANY,"%.2f", eff));
                accTimes[i] += time;
                accEff[i] += eff;
            }
        }
        List<Double> timeList = Arrays.stream(accTimes)
                .asDoubleStream()
                .map(k -> k/(double)(testAmount)).boxed().collect(Collectors.toList());

        List<Double> effList = Arrays.stream(accEff)
                .map(k -> k/(double)(testAmount)).boxed().collect(Collectors.toList());



        for (int i = 0; i < avgNumList.length; i++) {
            avgNumList[i] += timeList.get(i);
        }

        for (int i = 0; i < avgEffList.length; i++) {
            avgEffList[i] += effList.get(i);
        }

        List<String> timeRes = timeList.stream().mapToDouble(d -> d)
                                    .mapToObj(d -> String.format(Locale.GERMANY,"%.2f", d))
                                    .collect(Collectors.toList());

        List<String> effRes = effList.stream().mapToDouble(d -> d)
                .mapToObj(d -> String.format(Locale.GERMANY,"%.2f", d))
                .collect(Collectors.toList());

        List<String> resRuntime = new ArrayList<>();
        resRuntime.add(path);
        resRuntime.addAll(timeRes);

        List<String> resEff = new ArrayList<>();
        resEff.add(path);
        resEff.addAll(effRes);

        tableRowsRun.add(resRuntime);
        tableRowsEff.add(resEff);

        System.out.println(resultsRawEff.get(0).toString());

        FileWriter.writeCSV(resultsRawTime, path + "-Time");
        FileWriter.writeCSV(resultsRawEff, path + "-Eff");
    }





}
