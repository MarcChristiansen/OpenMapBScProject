package openmap.parsing;

import openmap.framework.Bounds;
import openmap.framework.Node;
import openmap.framework.OsmWay;
import openmap.framework.OsmParser;
import openmap.standard.BoundsImpl;
import openmap.standard.NodeImpl;
import openmap.standard.OsmWayImpl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.*;

/**
 * Implementation of the OSM xml parser
 */
public class OsmXmlParserImpl implements OsmParser {

    String fileIn;

    public OsmXmlParserImpl(String fileIn){
        this.fileIn = fileIn;
    }

    @Override
    public Map<Long, Node> parseNodes(Map<Long, Byte> nodeWayCounter) {
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
                            if(nodeWayCounter.getOrDefault(idLong, (byte)(0))>0){
                                Node node = new NodeImpl(idLong,
                                        Double.parseDouble(lat.getValue()),
                                        Double.parseDouble(lon.getValue()));
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
    public List<OsmWay> parseWays() {
        List<OsmWay> wayList = new ArrayList<OsmWay>();

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
                        if(currentTags.getOrDefault("highway", null) != null){
                            wayList.add(new OsmWayImpl(new ArrayList<Long>(nodeRefList), new HashMap<String, String>(currentTags)));
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

        return wayList;
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