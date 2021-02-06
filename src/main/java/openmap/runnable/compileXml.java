package openmap.runnable;

import openmap.framework.Node;
import openmap.framework.OsmWay;
import openmap.framework.OsmXmlParser;
import openmap.framework.graphBuilder;
import openmap.standard.GraphBuilderImpl;
import openmap.standard.OsmXmlParserImpl;

import java.util.List;
import java.util.Map;

public class compileXml {
    public static void main(String[] args) {
        String path = "C:\\testmap.osm";
        path = "C:\\testmap.osm";

        OsmXmlParser parser = new OsmXmlParserImpl("C:\\testmap.osm");
        graphBuilder graphBuilder = new GraphBuilderImpl(parser);

        /*
        List<OsmWay> WayList = parser.parseWays(path);
        Map<Long, Integer> nodeWayCounter = graphBuilder.countNodes(WayList);
        Map<Long, Node> NodeMap = parser.parseNodes(path, nodeWayCounter);
        */
    }
}
