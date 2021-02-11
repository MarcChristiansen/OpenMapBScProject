package openmap.utility;

import openmap.framework.Graph;
import openmap.framework.OsmXmlParser;
import openmap.standard.GraphBuilderImpl;
import openmap.standard.GraphImpl;
import openmap.standard.OsmXmlParserImpl;

import java.io.*;


public class XMLUtility {

    /***
     * Serialize a graph to a given file
     * Will create file if it does not already exist
     * @param graph The graph to serialize
     * @param outPath The path to the file where we want to save the graph
     * @throws IOException Might exception if filename mismatch or other mishaps
     */
    public static void createSerializedGraph(Graph graph, String outPath) throws IOException{
        graph.prepareForSerialization();

        FileOutputStream fos =
                new FileOutputStream(outPath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(graph);
        oos.close();
        fos.close();
    }

    /***
     * Load a graph from a given serialized file
     * @param path The path to the serialized graph
     * @return The graph found
     * @throws IOException File might not exist or other io related stuff
     * @throws ClassNotFoundException If serialized file is weird.
     */
    public static Graph loadGraph(String path) throws IOException, ClassNotFoundException{
        //Deserialization test
        Graph graph = null;

        FileInputStream fis = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(fis);
        graph = (GraphImpl) ois.readObject();
        ois.close();
        fis.close();

        graph.doDeserialization();

        return graph;
    }
}
