package openmap.benchmarks;

import openmap.framework.Graph;
import openmap.parsing.json.DiskUtility;
import openmap.utility.LatexUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkGraphSize {
    public static void main(String[] args) throws IOException {


        List<List<String>> data = new ArrayList<>();
        List<String> header = new ArrayList<>();

        header.add("Kort");
        header.add("Nodes");
        header.add("Edges");

        data.add(header);

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            data.add(BenchmarkSpecificMap(args[i]));
        }


        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("BenchmarkGraphSize.txt"), "utf-8"))) {
            writer.write(LatexUtility.generateStandardTable(data));
        }
    }

    private static List<String> BenchmarkSpecificMap(String path) {

        List<String> list = new ArrayList<>();

        Graph graph = null;
        try { graph = DiskUtility.loadJsonGraph(path); }
        catch (Exception e){ e.printStackTrace(); }

        list.add(path);
        list.add(graph.getNodeCount()+"");
        list.add(graph.getEdgeCount()+"");

        return list;
    }
}
