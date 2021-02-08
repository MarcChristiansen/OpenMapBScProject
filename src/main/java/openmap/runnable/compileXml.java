package openmap.runnable;

import openmap.framework.Node;
import openmap.framework.OsmXmlParser;
import openmap.framework.graphBuilder;
import openmap.standard.GraphBuilderImpl;
import openmap.standard.OsmXmlParserImpl;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class compileXml {
    public static void main(String[] args) {
        String path = "D:\\denmark-latest.osm\\denmark-latest.osm";
        //path = "C:\\testmapInter.osm";

        OsmXmlParser parser = new OsmXmlParserImpl(path);
        graphBuilder graphBuilder = new GraphBuilderImpl(parser);

        Map<Long, Node> graph = graphBuilder.createGraph();

        for (Map.Entry<Long, Node> entry : graph.entrySet())
        {
            System.out.println("key: " + entry.getKey());

            System.out.println("-> " + Arrays.toString(entry.getValue().getPaths().toArray()));
        }

        System.out.println(graph.size());


        //Serialization from https://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
        //We just had to check of Serialization would work.
        try
        {
            FileOutputStream fos =
                    new FileOutputStream("hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject((HashMap<Long, Node>)graph);
            oos.close();
            fos.close();
            System.out.println("Serialized HashMap data is saved in hashmap.ser");

        }catch(Exception ioe)
        {
            ioe.printStackTrace();
        }

        //Deserialization test
        HashMap<Long, Node> map = null;
        try
        {
            FileInputStream fis = new FileInputStream("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
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

        for (Map.Entry<Long, Node> entry : map.entrySet())
        {
            System.out.println("key: " + entry.getKey());
            Node n1 = entry.getValue().getPaths().get(0).getDestination();
            Node n2 = map.get(entry.getValue().getPaths().get(0).getDestination().getId());
            System.out.println(n1 == n2); //Since this returns true it means references match and we can actually use this. (It does so yay)
        }

    }
}
