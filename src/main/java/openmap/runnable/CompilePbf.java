package openmap.runnable;

import openmap.framework.Graph;
import openmap.framework.OsmParser;
import openmap.parsing.ParsingUtil;
import openmap.standard.GraphBuilderImpl;
import openmap.parsing.OsmiumPbfParserImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static openmap.parsing.json.DiskUtility.createJsonGraph;

/**
 * Simple class to help create xml files from snippets we use
 */
public class CompilePbf {
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



        OsmParser parser = new OsmiumPbfParserImpl(path, ParsingUtil.getMinimizedAllowedValues());
        openmap.framework.graphBuilder graphBuilder = new GraphBuilderImpl(parser);
        ((GraphBuilderImpl)graphBuilder).setShouldOptimizeGraph(true);


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
