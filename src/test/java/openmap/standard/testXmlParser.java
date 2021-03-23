package openmap.standard;

import openmap.framework.*;

import openmap.parsing.OsmXmlParserImpl;
import openmap.parsing.ParsingUtil;
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
        parser = new OsmXmlParserImpl(resourceDirectory.toFile().getAbsolutePath(), ParsingUtil.getDefaultAllowedValues());
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
        Map<Long, Byte> nodeWaysCounter = new HashMap<>();
        nodeWaysCounter.put(1156449100L, (byte)(1));
        nodeWaysCounter.put(1156449059L, (byte)(2));
        nodeWaysCounter.put(1156448987L, (byte)(1));
        nodeWaysCounter.put(1156449064L, (byte)(1));
        nodeWaysCounter.put(1632757147L, (byte)(1));
        nodeWaysCounter.put(2140481886L, (byte)(1));
        nodeWaysCounter.put(2140481884L, (byte)(1));
        nodeWaysCounter.put(2140481883L, (byte)(1));
        nodeWaysCounter.put(2140481887L, (byte)(1));
        nodeWaysCounter.put(1156449038L, (byte)(1));
        nodeWaysCounter.put(1511529774L, (byte)(1));
        nodeWaysCounter.put(1156449155L, (byte)(1));

        assert(parser.parseNodes(nodeWaysCounter).size() == 12);
    }

}

