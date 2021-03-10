package openmap.runnable;

import openmap.framework.*;
import openmap.standard.DijkstraImpl;
import openmap.standard.GraphBuilderImpl;
import openmap.standard.OsmXmlParserImpl;
import openmap.JsonParsing.DiskUtility;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DijkstraDenmarkTest {
    public static void main(String[] args) {
        String path = "C:\\denmark-latest.osm";
        path = "C:\\motorwayTest.osm";
        path = "F:\\AU\\Open map data\\denmark-latest.osm";
        //path = "C:\\testmapInter.osm";

        OsmParser parser = new OsmXmlParserImpl(path);
        graphBuilder graphBuilder = new GraphBuilderImpl(parser);

        Graph graph = graphBuilder.createGraph();

        for (Map.Entry<Long, Node> entry : graph.getNodeMap().entrySet())
        {
            System.out.println("key: " + entry.getKey());

            System.out.println("-> " + Arrays.toString(entry.getValue().getPaths().toArray()));
        }

        //Serialization from https://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
        //We just had to check of Serialization would work.


        try
        {
            DiskUtility.createSerializedGraph(graph, "hashmap.ser");

        }catch(Exception ioe)
        {
            ioe.printStackTrace();
        }

        //Deserialization test
        Graph map = null;
        try
        {
            map = DiskUtility.loadGraph("hashmap.ser");
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }

        System.out.println("beginning Dijkstra");
        PathFinder pf = new DijkstraImpl(map);

        List<Long> shortPath = pf.getShortestPath(5981056648L, 4689225871L);

        shortPath.forEach(System.out::println);

        //for (Map.Entry<Long, Node> entry : map.getNodeMap().entrySet())
        //{
        //    System.out.println("key: " + entry.getKey());
        //    System.out.println("-> " + Arrays.toString(entry.getValue().getPaths().toArray()));
        //}

    }
}
