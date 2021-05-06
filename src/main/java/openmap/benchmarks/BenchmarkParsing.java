package openmap.benchmarks;

import openmap.framework.OsmParser;
import openmap.parsing.OsmiumPbfParserImpl;
import openmap.parsing.ParsingUtil;
import openmap.standard.GraphBuilderImpl;

import java.io.IOException;

public class BenchmarkParsing {
    public static void main(String[] args) throws IOException {
        BenchmarkDualVsTriplePass();
    }


    public static void BenchmarkDualVsTriplePass(){
        int testAmount = 10; //Number of tests to run

        long accTimeDualPass = 0;
        long accTimeTriplePass = 0;


        for (int i = 0; i < testAmount; i++) {
            long start = System.currentTimeMillis();

            OsmParser parser = new OsmiumPbfParserImpl("C:\\denmark-latest.osm.pbf", ParsingUtil.getDefaultAllowedValues());
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

            OsmParser parser = new OsmiumPbfParserImpl("C:\\denmark-latest.osm.pbf", ParsingUtil.getDefaultAllowedValues());
            parser.CacheWays(false);

            GraphBuilderImpl graphBuilder = new GraphBuilderImpl(parser);
            graphBuilder.SetOptimizationLevel(0);
            graphBuilder.createGraph();

            long finish = System.currentTimeMillis();
            accTimeTriplePass += finish - start;
        }

        System.out.println(accTimeDualPass/testAmount);
        System.out.println(accTimeTriplePass/testAmount);

    }



}
