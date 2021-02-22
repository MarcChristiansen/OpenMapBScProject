package openmap.runnable;

import openmap.framework.Graph;
import openmap.framework.OsmXmlParser;
import openmap.standard.GraphBuilderImpl;
import openmap.standard.OsmXmlParserImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static openmap.JsonParsing.DiskUtility.createSerializedGraph;

/**
 * Simple class to help create xml files from snippets we use
 */
public class CompileXML {
    public static void main(String[] args) throws IOException {
        String path = "";
        String outPath = "";

        if(args != null && args.length == 2){
            path = args[0];
            outPath = args[1];
        }
        else{

            path = readLine(
                    "Enter osm path : ");
            outPath = readLine(
                    "Enter ser path : ");
        }

        OsmXmlParser parser = new OsmXmlParserImpl(path);
        openmap.framework.graphBuilder graphBuilder = new GraphBuilderImpl(parser);

        Graph graph = graphBuilder.createGraph();


        createSerializedGraph(graph, outPath);
        System.out.println("Serialized graph data is saved in " + outPath);

    }

    private static String readLine(String format, Object... args) throws IOException {
        System.out.print(String.format(format, args));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
}
