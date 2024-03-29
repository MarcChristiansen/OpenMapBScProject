package openmap.parsing;

import openmap.framework.Bounds;
import openmap.framework.Node;
import openmap.framework.OsmWay;
import openmap.framework.OsmParser;
import openmap.standard.BoundsImpl;
import openmap.standard.OsmWayImpl;
import openmap.special.ParsingNodeImpl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * OSM file parser implemented using an XML reader.
 * Reads XML .osm files.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-02-2021
 */
public class OsmXmlParserImpl implements OsmParser {

    List<OsmWay> osmWays;
    String fileIn;
    List<String> highWayFilter;

    //Flags
    boolean shouldCacheWaysInRam = true;


    public OsmXmlParserImpl(String fileIn, List<String> highWayFilter){
        this.fileIn = fileIn;
        this.highWayFilter = highWayFilter;
    }

    @Override
    public Map<Long, Node> parseNodes(Map<Long, Byte> nodeWayCounter) {
        return parseNodes(nodeWayCounter, 0);
    }

    @Override
    public Map<Long, Node> parseNodes(Map<Long, Byte> nodeWayCounter, int minConnections) {
        Map<Long, Node> NodeMap = new HashMap<Long, Node>();

        XMLEventReader reader = getReader();

        try {
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "node":
                            //Load in necessary attributes
                            Attribute id = startElement.getAttributeByName(new QName("id"));
                            Attribute lat = startElement.getAttributeByName(new QName("lat"));
                            Attribute lon = startElement.getAttributeByName(new QName("lon"));
                            if(id == null || lat == null || lon == null)
                            {
                                throw new XMLStreamException();
                            }

                            //Check if node is in the nodeWayCounter
                            long idLong = Long.parseLong(id.getValue());
                            byte wayCount = nodeWayCounter.getOrDefault(idLong, (byte)(0));
                            if(wayCount>minConnections){
                                Node node = new ParsingNodeImpl(idLong,
                                        Double.parseDouble(lat.getValue()),
                                        Double.parseDouble(lon.getValue()),
                                        wayCount);
                                //add to nodemap
                                NodeMap.put(idLong, node);
                            }
                            break;
                        case "way":
                            break;
                        case "nd":
                            break;
                        case "relation":
                            break;
                        case "status":
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
        return NodeMap;
    }

    @Override
    public Bounds parseBounds() {
        Bounds bounds = null;

        XMLEventReader reader = getReader();


        //List<Long> nodeRefList = new ArrayList<Long>();
        //Map<String, String> currentTags = new HashMap<String, String>();


        try {
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "bounds":
                            Attribute minLat = startElement.getAttributeByName(new QName("minlat"));
                            Attribute minLon = startElement.getAttributeByName(new QName("minlon"));
                            Attribute maxLat = startElement.getAttributeByName(new QName("maxlat"));
                            Attribute maxLon = startElement.getAttributeByName(new QName("maxlon"));
                            if(minLat == null || minLon == null || maxLat == null || maxLon == null)
                            {
                                throw new XMLStreamException();
                            }

                            //Check if node is in the nodeWayCounter
                            bounds = new BoundsImpl(
                                    Double.parseDouble(minLat.getValue()),
                                    Double.parseDouble(minLon.getValue()),
                                    Double.parseDouble(maxLat.getValue()),
                                    Double.parseDouble(maxLon.getValue())
                            );
                            break;
                        default:
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        return bounds;
    }

    @Override
    public void CacheWays(boolean shouldCacheWays) {
        this.shouldCacheWaysInRam = shouldCacheWays;
    }

    /**
     * Parses all ways and possibly runs an action. If action is null then osmWays is populated with a list of all ways.
     * @param action A lambda that takes a way, this will be run on all paths.
     */
    public void parseWays(Consumer<OsmWay> action) {

        List<OsmWay> wayList = null;
        if(action == null){
            wayList = new ArrayList<OsmWay>();
        }


        XMLEventReader reader = getReader();


        List<Long> nodeRefList = new ArrayList<Long>();
            Map<String, String> currentTags = new HashMap<String, String>();


        try {
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "node":
                            break;
                        case "way":
                            nodeRefList.clear();
                            currentTags.clear();
                            break;
                        case "nd":
                            Attribute refId = startElement.getAttributeByName(new QName("ref"));
                            if(refId == null)
                            {
                                throw new XMLStreamException();
                            }
                            nodeRefList.add(Long.parseLong(refId.getValue()));
                            break;
                        case "tag":
                            Attribute k = startElement.getAttributeByName(new QName("k"));
                            Attribute v = startElement.getAttributeByName(new QName("v"));
                            if(k == null || v == null)
                            {
                                throw new XMLStreamException();
                            }
                            currentTags.put(k.getValue(),v.getValue());
                            break;
                        case "relation":
                            break;
                        case "status":
                            break;
                    }
                }
                if (nextEvent.isEndElement()){
                    EndElement endelement = nextEvent.asEndElement();
                    if(endelement.getName().getLocalPart().equals("way")){
                        //create new OSM way and add to list, if the way is a highway
                        if(highWayFilter.stream().anyMatch(currentTags.getOrDefault("highway", "")::equalsIgnoreCase)){
                            OsmWay newWay = new OsmWayImpl(new ArrayList<Long>(nodeRefList), new HashMap<String, String>(currentTags));
                            if(action != null){
                                action.accept(newWay);
                            }else{
                                wayList.add(newWay);
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        //If the action existed we do not cache the way.
        osmWays = wayList;
    }

    @Override
    public void runWithAllWays(Consumer<OsmWay> action) {
        if(shouldCacheWaysInRam){
            if(osmWays == null) { parseWays(null);}
            osmWays.forEach(action);
        }
        else parseWays(action);
    }

    private XMLEventReader getReader() {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = null;

        try {
            reader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileIn));
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return reader;
    }
}
