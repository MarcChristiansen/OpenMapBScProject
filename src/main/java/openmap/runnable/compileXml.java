package openmap.runnable;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.OsmXmlParser;
import openmap.framework.graphBuilder;
import openmap.standard.GraphBuilderImpl;
import openmap.standard.GraphImpl;
import openmap.standard.NodeImpl;
import openmap.standard.OsmXmlParserImpl;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class compileXml {
    public static void main(String[] args) {
        String path = "C:\\denmark-latest.osm";
        path = "C:\\testmapInter.osm";

        OsmXmlParser parser = new OsmXmlParserImpl(path);
        graphBuilder graphBuilder = new GraphBuilderImpl(parser);

        Graph graph = graphBuilder.createGraph();

        for (Map.Entry<Long, Node> entry : graph.getNodeMap().entrySet())
        {
            System.out.println("key: " + entry.getKey());

            System.out.println("-> " + Arrays.toString(entry.getValue().getPaths().toArray()));
        }


        //Serialization from https://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
        //We just had to check of Serialization would work.

        graph.prepareForSerialization();

        try
        {
            FileOutputStream fos =
                    new FileOutputStream("hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
            oos.close();
            fos.close();
            System.out.println("Serialized HashMap data is saved in hashmap.ser");

        }catch(Exception ioe)
        {
            ioe.printStackTrace();
        }

        //Deserialization test
        Graph map = null;
        try
        {
            FileInputStream fis = new FileInputStream("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (GraphImpl) ois.readObject();
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

        System.out.println("Loaded... processing");
        map.doDeserialization();


        for (Map.Entry<Long, Node> entry : map.getNodeMap().entrySet())
        {
            System.out.println("key: " + entry.getKey());
            Node n1 = entry.getValue().getPaths().get(0).getDestination();
            Node n2 = map.getNodeMap().get(entry.getValue().getPaths().get(0).getDestination().getId());
            System.out.println((n1 == n2) +  " " + n1); //Since this returns true it means references match and we can actually use this.
            System.out.println("-> " + Arrays.toString(entry.getValue().getPaths().toArray()));
        }

    }
}
