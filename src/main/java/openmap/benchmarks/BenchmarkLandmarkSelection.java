package openmap.benchmarks;

import openmap.framework.Graph;
import openmap.gui.LandmarkSelectionUtility;
import openmap.parsing.json.DiskUtility;
import openmap.utility.LatexUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkLandmarkSelection {
    public static void main(String[] args) throws IOException {


        List<List<String>> data = new ArrayList<>();
        List<String> header = new ArrayList<>();

        header.add("Kort");
        header.add("Farthest (ms)");
        header.add("Randomized (ms)");

        data.add(header);

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            data.add(BenchmarkSpecificMap(args[i]));
        }


        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("BenchmarkLandmarkSelection.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(data));
        }
    }

    private static List<String> BenchmarkSpecificMap(String path) {

        List<String> list = new ArrayList<>();

        Graph graph = null;
        try { graph = DiskUtility.loadJsonGraph(path); }
        catch (Exception e){ e.printStackTrace(); }

        LandmarkSelectionUtility lfsu = new LandmarkSelectionUtility(graph);

        list.add(path);

        for(String s : lfsu.getLandmarkSelectionStrings()){
            lfsu.getLandmarkSelector(s).findLandmarks(20);
            list.add(lfsu.getLandmarkSelector(s).getExecutionTime()+"");
        }

        return list;
    }
}
