package openmap.benchmarks;

import openmap.framework.OsmParser;
import openmap.parsing.OsmXmlParserImpl;
import openmap.parsing.OsmiumPbfParserImpl;
import openmap.parsing.ParsingUtil;
import openmap.standard.GraphBuilderImpl;
import openmap.utility.LatexUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BenchmarkStaxVsOsmosis {
    public static void main(String[] args) throws IOException {
        List<List<String>> tableRows = new ArrayList<>();
        String[] names = {"Kort", "StAX tid (s)", "Osmosis tid (s)", "Forskel (\\%)"};
        tableRows.add(Arrays.asList(names));

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            tableRows.add(Benchmark(args[i]));
        }

        System.out.println("\n###### Latex full table ######");
        System.out.println(LatexUtility.generateStandardTable(tableRows));

    }

    public static List<String> Benchmark(String path){
        int testAmount = 3; //Number of tests to run

        String osmPath = path + ".osm";
        String pbfPath = path + ".osm.pbf";

        long accTimeStAX = 0;
        long accTimeOsmosis = 0;


        for (int i = 0; i < testAmount; i++) {
            long start = System.currentTimeMillis();

            OsmParser parser = new OsmXmlParserImpl(osmPath, ParsingUtil.getDefaultAllowedValues());
            parser.CacheWays(false);

            //GraphBuilder creation
            GraphBuilderImpl graphBuilder = new GraphBuilderImpl(parser);
            graphBuilder.SetOptimizationLevel(0);
            graphBuilder.createGraph();

            long finish = System.currentTimeMillis();
            accTimeStAX += finish - start;
        }
        //If we want a triple pass we do not want to cache ways.

        for (int i = 0; i < testAmount; i++) {
            long start = System.currentTimeMillis();

            OsmParser parser = new OsmiumPbfParserImpl(pbfPath, ParsingUtil.getDefaultAllowedValues());
            parser.CacheWays(false);

            GraphBuilderImpl graphBuilder = new GraphBuilderImpl(parser);
            graphBuilder.SetOptimizationLevel(0);
            graphBuilder.createGraph();

            long finish = System.currentTimeMillis();
            accTimeOsmosis += finish - start;
        }

        System.out.println(path);
        System.out.println(accTimeStAX/testAmount);
        System.out.println(accTimeOsmosis/testAmount);
        System.out.println();

        double dualtime = (double)(accTimeStAX)/(double)(testAmount)/1000;
        double tripletime = (double)(accTimeOsmosis)/(double)(testAmount)/1000;
        double percentDiff = (((double)(tripletime-dualtime))/((double)(dualtime)))*100 ;

        List<String> res = new ArrayList<>();

        res.add(path);
        res.add(Double.toString(dualtime));
        res.add(Double.toString(tripletime));
        res.add(Double.toString(percentDiff));

        return res;
    }



}
