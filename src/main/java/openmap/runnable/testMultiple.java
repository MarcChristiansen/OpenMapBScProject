package openmap.runnable;

import openmap.benchmarks.BenchmarkPathfindersSimple;
import openmap.benchmarks.TestLandmarks;
import openmap.benchmarks.rankBenchmarks.DijkstraRankBenchmark;
import openmap.utility.LatexUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class testMultiple {

    public static void main(String[] args) throws IOException {

        String[] pathfinder = {"dkfinal.json", "nlfinal.json", "swefinal.json", "gbfinal.json", "gerfinal.json", "eufinal.json"};
        //String[] pathfinder = {"map3.json", "map2.json"};
        System.out.println("running benchmark Pathfinders");
        BenchmarkPathfindersSimple.main(pathfinder);

        String[] landmarks = {"dkfinal.json", "-1", "250"};
        //String[] landmarks = {"map3.json", "-1", "1"};
        System.out.println("running test landmarks");
        TestLandmarks.main(landmarks);

        String[] dijkstraRank = {"gerfinal.json"};
        System.out.println("dijkstra rank");
        //String[] dijkstraRank = {"map3.json"};
        DijkstraRankBenchmark.main(dijkstraRank);


    }
    //Testlandmarks
    //        PathfindersSimpelbenchmark
    //Dijkstra rank benchmark

}
