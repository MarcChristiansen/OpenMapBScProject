package openmap.runnable;

import openmap.framework.Graph;
import openmap.framework.OsmParser;
import openmap.parsing.OsmXmlParserImpl;
import openmap.parsing.ParsingUtil;
import openmap.standard.GraphBuilderImpl;
import openmap.parsing.OsmiumPbfParserImpl;
import org.openstreetmap.osmosis.osmbinary.file.FileFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static openmap.parsing.json.DiskUtility.createJsonGraphJGen;

/**
 * Simple class to help create xml files from snippets we use
 */
public class CompileMapFile {
    /**
     * Main method to compile a map file (.osm / .pbf) to a graph
     * @param args String array of command line arguments. First argument is path to map file, second argument is
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String path = "";
        String outPath = "";
        int optimLevel = -1;
        String wayTypeListSelection = "";
        boolean shouldUseTriplePass = true;


        if(args != null && args.length == 5){
            path = args[0];
            outPath = args[1];
            optimLevel = Integer.parseInt(args[2]);
            wayTypeListSelection = args[3];
        }
        else{

            path = readLine(
                    "Enter osm path : ");

            outPath = readLine(
                    "Enter out path : ");

            optimLevel = Integer.parseInt(readLine(
                    "Enter optimization level (0, 1 or 2) : "));

            wayTypeListSelection = readLine(
                    "Enter path type set (normal or mini) : ");

            shouldUseTriplePass = Boolean.parseBoolean(readLine(
                    "Should we use a triple pass? (true/false)"));
        }
        OsmParser parser = null;
        String extension = getFileExtension(path);

        List<String> wayFilter = ParsingUtil.getAllowedValues(wayTypeListSelection);

        if(extension.equals("osm")){
            parser = new OsmXmlParserImpl(path, wayFilter);
        }
        else if(extension.equals("pbf")){
            parser = new OsmiumPbfParserImpl(path, wayFilter);
        }
        else{
            throw new FileFormatException("File needs to be either pbf or osm format. a " + extension + " file was given.");
        }

        //If we want a triple pass we do not want to cache ways.
        parser.CacheWays(!shouldUseTriplePass);

        //GraphBuilder creation
        GraphBuilderImpl graphBuilder = new GraphBuilderImpl(parser);
        graphBuilder.SetOptimizationLevel(optimLevel);

        //Graph creation and graph saving
        Graph graph = graphBuilder.createGraph();
        createJsonGraphJGen(graph, outPath);
        System.out.println("Serialized graph data is saved in " + outPath);
    }

    private static String getFileExtension(String path) {
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }
        return extension;
    }

    private static String readLine(String format, Object... args) throws IOException {
        System.out.print(String.format(format, args));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
}
