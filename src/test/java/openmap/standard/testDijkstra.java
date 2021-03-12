package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.OsmParser;
import openmap.framework.PathFinder;
import openmap.parsing.OsmXmlParserImpl;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class testDijkstra {

    OsmParser parser;
    Graph graph;

    @Before
    public void setUp() {
        Path resourceDirectory = Paths.get("src","test","resources", "testOsmFiles", "testMapInter.osm");
        parser = new OsmXmlParserImpl(resourceDirectory.toFile().getAbsolutePath());

        //create nodemap
        Map<Long, Node> nodeMap = new HashMap<Long, Node>();
        //Create nodes
        /*
            XXXXXXX
            X1-2-3X
            X|X|XXX
            X4-5XXX
            XXXXXXX
         */
        Node node1 = new NodeImpl(1, 0, 0);
        Node node2 = new NodeImpl(2, 0, 0);
        Node node3 = new NodeImpl(3, 0, 0);
        Node node4 = new NodeImpl(4, 0, 0);
        Node node5 = new NodeImpl(5, 0, 0);
        node1.addPath(new StandardPathImpl(node2, 10.0));
        node1.addPath(new StandardPathImpl(node4, 2.0));
        node2.addPath(new StandardPathImpl(node1, 10.0));
        node2.addPath(new StandardPathImpl(node3, 2.0));
        node2.addPath(new StandardPathImpl(node5, 2.0));
        node3.addPath(new StandardPathImpl(node2, 2.0));
        node4.addPath(new StandardPathImpl(node1, 2.0));
        node4.addPath(new StandardPathImpl(node5, 2.0));
        node5.addPath(new StandardPathImpl(node2, 2.0));
        node5.addPath(new StandardPathImpl(node4, 2.0));

        //add nodes to nodemap
        nodeMap.put(node1.getId(), node1);
        nodeMap.put(node2.getId(), node2);
        nodeMap.put(node3.getId(), node3);
        nodeMap.put(node4.getId(), node4);
        nodeMap.put(node5.getId(), node5);
        graph = new GraphImpl(nodeMap, null);
    }

    @Test
    public void testDijkstraShouldreturnList32541(){
        PathFinder pf = new DijkstraImpl(graph);

        List<Long> idList = pf.getShortestPath((long)1, (long)3);
        assert(idList.get(0) == (long)1);
        assert(idList.get(1) == (long)4);
        assert(idList.get(2) == (long)5);
        assert(idList.get(3) == (long)2);
        assert(idList.get(4) == (long)3);
    }

}

