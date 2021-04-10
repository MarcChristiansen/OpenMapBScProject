package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.OsmParser;
import openmap.framework.PathFinder;
import openmap.parsing.OsmXmlParserImpl;
import openmap.parsing.ParsingUtil;
import openmap.parsing.json.DiskUtility;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class testDecodedGraph {

    OsmParser parser;
    Graph graph;

    @Before
    public void setUp() throws IOException, ParseException {
        Path resourceDirectory = Paths.get("src","test","resources", "testOsmFiles", "test.json");

        graph = DiskUtility.loadJsonGraph(resourceDirectory.toString());
    }

    @Test
    public void TestIncomingPathsAreCorrectlySetup(){
        graph.getNodeMap().values().forEach(n -> {
            n.getOutgoingPaths().forEach(p ->{
                assert(p.getSource().getIncomingPaths().contains(p));
            });
        });
    }

}


