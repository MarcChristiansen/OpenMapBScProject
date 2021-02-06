package openmap.runnable;

import openmap.framework.OsmXmlParser;
import openmap.standard.OsmXmlParserImpl;

public class compileXml {
    public static void main(String[] args) {
        String path = "C:\\testmap.osm";
        path = "D:\\denmark-latest.osm\\denmark-latest.osm";

        OsmXmlParser parser = new OsmXmlParserImpl();

        parser.parse(path);
    }
}
