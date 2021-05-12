package openmap.benchmarks;

import openmap.framework.OsmParser;
import openmap.parsing.OsmiumPbfParserImpl;
import openmap.parsing.ParsingUtil;
import openmap.standard.GraphBuilderImpl;
import openmap.utility.LatexUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BenchmarkParsing {
    public static void main(String[] args) throws IOException {
        List<List<String>> tableRows = new ArrayList<>();
        String[] names = {"Kort", "To passes (ms)", "Tre passes (ms)", "Forskel (\\%)"};
        tableRows.add(Arrays.asList(names));

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
            tableRows.add(BenchmarkDualVsTriplePass(args[i]));
        }

        System.out.println("\n###### Latex full table ######");
        System.out.println(LatexUtility.generateStandardTable(tableRows));

    }

    public static List<String> BenchmarkDualVsTriplePass(String path){
        int testAmount = 5; //Number of tests to run

        long accTimeDualPass = 0;
        long accTimeTriplePass = 0;


        for (int i = 0; i < testAmount; i++) {
            long start = System.currentTimeMillis();

            OsmParser parser = new OsmiumPbfParserImpl(path, ParsingUtil.getDefaultAllowedValues());
            parser.CacheWays(true);

            //GraphBuilder creation
            GraphBuilderImpl graphBuilder = new GraphBuilderImpl(parser);
            graphBuilder.SetOptimizationLevel(0);
            graphBuilder.createGraph();

            long finish = System.currentTimeMillis();
            accTimeDualPass += finish - start;
        }
        //If we want a triple pass we do not want to cache ways.

        for (int i = 0; i < testAmount; i++) {
            long start = System.currentTimeMillis();

            OsmParser parser = new OsmiumPbfParserImpl(path, ParsingUtil.getDefaultAllowedValues());
            parser.CacheWays(false);

            GraphBuilderImpl graphBuilder = new GraphBuilderImpl(parser);
            graphBuilder.SetOptimizationLevel(0);
            graphBuilder.createGraph();

            long finish = System.currentTimeMillis();
            accTimeTriplePass += finish - start;
        }

        System.out.println(path);
        System.out.println(accTimeDualPass/testAmount);
        System.out.println(accTimeTriplePass/testAmount);
        System.out.println();

        long dualtime = accTimeDualPass/testAmount;
        long tripletime = accTimeTriplePass/testAmount;
        double percentDiff = (((double)(tripletime-dualtime))/((double)(dualtime)))*100 ;

        List<String> res = new ArrayList<>();

        res.add(path);
        res.add(Long.toString(dualtime));
        res.add(Long.toString(tripletime));
        res.add(Double.toString(percentDiff));

        return res;
    }



}
