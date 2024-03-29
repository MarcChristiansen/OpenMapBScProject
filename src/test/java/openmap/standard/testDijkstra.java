package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.OsmParser;
import openmap.framework.PathFinder;
import openmap.parsing.OsmXmlParserImpl;
import openmap.parsing.ParsingUtil;
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
        parser = new OsmXmlParserImpl(resourceDirectory.toFile().getAbsolutePath(), ParsingUtil.getDefaultAllowedValues());

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
        node1.addOutgoingPath(new StandardPathImpl(node2, node1, 10.0));
        node1.addOutgoingPath(new StandardPathImpl(node4, node1,2.0));
        node2.addOutgoingPath(new StandardPathImpl(node1, node2,10.0));
        node2.addOutgoingPath(new StandardPathImpl(node3, node2,2.0));
        node2.addOutgoingPath(new StandardPathImpl(node5, node2,2.0));
        node3.addOutgoingPath(new StandardPathImpl(node2, node3,2.0));
        node4.addOutgoingPath(new StandardPathImpl(node1, node4,2.0));
        node4.addOutgoingPath(new StandardPathImpl(node5, node4,2.0));
        node5.addOutgoingPath(new StandardPathImpl(node2, node5,2.0));
        node5.addOutgoingPath(new StandardPathImpl(node4, node5,2.0));

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

        List<Node> idList = pf.getShortestPath(graph.getNodeMap().get(1L), graph.getNodeMap().get(3L));
        assert(idList.get(0).getId() == 1L);
        assert(idList.get(1).getId() == 4L);
        assert(idList.get(2).getId() == 5L);
        assert(idList.get(3).getId() == 2L);
        assert(idList.get(4).getId() == 3L);
    }

    @Test
    public void testWithSameSourceAndDest(){
        PathFinder pf = new DijkstraImpl(graph);

        List<Node> idList = pf.getShortestPath(graph.getNodeMap().get(1L), graph.getNodeMap().get(1L));
        assert(idList.size() == 1);

    }

}

