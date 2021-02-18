package openmap.runnable;

import openmap.framework.Graph;
import openmap.framework.OsmXmlParser;
import openmap.standard.GraphBuilderImpl;
import openmap.standard.OsmXmlParserImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static openmap.utility.XMLUtility.createJsonGraph;
import static openmap.utility.XMLUtility.createSerializedGraph;

public class CompileJson {
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
                    "Enter json path : ");
        }

        OsmXmlParser parser = new OsmXmlParserImpl(path);
        openmap.framework.graphBuilder graphBuilder = new GraphBuilderImpl(parser);

        //((GraphBuilderImpl)graphBuilder).setShouldOptimizeGraph(false);
        Graph graph = graphBuilder.createGraph();


        createJsonGraph(graph, outPath);
        System.out.println("Serialized graph data is saved in " + outPath);

    }

    private static String readLine(String format, Object... args) throws IOException {
        System.out.print(String.format(format, args));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
}
