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
import java.util.ArrayList;
import java.util.List;

import static openmap.parsing.json.DiskUtility.createJsonGraph;
import static openmap.parsing.json.DiskUtility.createJsonGraphJGen;

/**
 * Simple class to help create xml files from snippets we use
 */
public class CompileMapFile {
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
        OsmParser parser = null;
        String extension = getFileExtension(path);

        List<String> wayFilter = ParsingUtil.getDefaultAllowedValues();

        if(extension.equals("osm")){
            parser = new OsmXmlParserImpl(path, wayFilter);
        }
        else if(extension.equals("pbf")){
            parser = new OsmiumPbfParserImpl(path, wayFilter);
        }
        else{
            throw new FileFormatException("File needs to be either pbf or osm format. a " + extension + " file was given.");
        }

        openmap.framework.graphBuilder graphBuilder = new GraphBuilderImpl(parser);
        ((GraphBuilderImpl)graphBuilder).setShouldOptimizeGraph(false);


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
