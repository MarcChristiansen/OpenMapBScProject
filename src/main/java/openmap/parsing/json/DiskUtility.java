package openmap.parsing.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import openmap.framework.Graph;
import openmap.standard.GraphImpl;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;


public class DiskUtility {

    /***
     * Serialize a graph to a json file
     * Will create file if it does not already exist
     * @param graph The graph to serialize
     * @param outPath The path to the file where we want to save the graph
     * @throws IOException Might exception if filename mismatch or other mishaps
     */
    public static void createJsonGraph(Graph graph, String outPath) throws IOException{

        try (FileWriter file = new FileWriter(outPath)) {
            graph.getJSONObject().writeJSONString(file);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***
     * Serialize a graph to a json file using the stream method with a jgenerator
     * Will create file if it does not already exist
     * @param graph The graph to serialize
     * @param outPath The path to the file where we want to save the graph
     * @throws IOException Might exception if filename mismatch or other mishaps
     */
    public static void createJsonGraphJGen(Graph graph, String outPath) throws IOException{

        try (FileOutputStream stream = new FileOutputStream(outPath)) {
            JsonFactory jFactory = new JsonFactory();
            JsonGenerator jGenerator = jFactory
                    .createGenerator(stream, JsonEncoding.UTF8);

            graph.WriteToJsonGenerator(jGenerator);

            jGenerator.flush();
            jGenerator.close();
            stream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***
     * Load a graph from a given json file
     * @param path The path to the serialized graph
     * @return The graph found
     * @throws IOException File might not exist or other io related stuff
     * @throws ParseException Something went wrong when parsing
     */
    public static Graph loadJsonGraph(String path) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();

        FileReader reader2 = new FileReader(path);
        //Read JSON file
        GraphContentHandler graphHandler = new GraphContentHandler();

        jsonParser.parse(reader2, graphHandler);
        return new GraphImpl(graphHandler.getNodeList(), graphHandler.getBounds());
        //reader2.close();

        /*FileReader reader = new FileReader(path);
        Object obj = jsonParser.parse(reader);

        return new GraphImpl((JSONObject) obj);*/
    }
}
