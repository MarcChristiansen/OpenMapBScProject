package openmap.standard;

import openmap.framework.*;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class testXmlParser {

    OsmParser parser;

    @Before
    public void setUp() {
        Path resourceDirectory = Paths.get("src","test","resources", "testOsmFiles", "testMapInter.osm");
        parser = new OsmXmlParserImpl(resourceDirectory.toFile().getAbsolutePath());
    }

    @Test
    public void testSetShouldOnlyHave2HighWays(){
        assert(parser.parseWays().size() == 2); //We only count highways
    }

    /**
     * Bit of a weird test, but ensures our implementation actually reads the correct number of nodes given a correct map.
     * Also ensures we might be able to change how we create our map without bricking this test.
     */
    @Test
    public void ShouldLoad12Nodes(){

        Map<Long, Integer> nodeWaysCounter = new HashMap<>();
        nodeWaysCounter.put(1156449100L, 1);
        nodeWaysCounter.put(1156449059L, 2);
        nodeWaysCounter.put(1156448987L, 1);
        nodeWaysCounter.put(1156449064L, 1);
        nodeWaysCounter.put(1632757147L, 1);
        nodeWaysCounter.put(2140481886L, 1);
        nodeWaysCounter.put(2140481884L, 1);
        nodeWaysCounter.put(2140481883L, 1);
        nodeWaysCounter.put(2140481887L, 1);
        nodeWaysCounter.put(1156449038L, 1);
        nodeWaysCounter.put(1511529774L, 1);
        nodeWaysCounter.put(1156449155L, 1);

        assert(parser.parseNodes(nodeWaysCounter).size() == 12);
    }

}

