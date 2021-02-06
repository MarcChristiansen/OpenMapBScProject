package openmap.standard;

import openmap.framework.Node;
import openmap.framework.OsmWay;
import openmap.framework.OsmXmlParser;

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
public class OsmXmlParserImpl implements OsmXmlParser {

    @Override
    public Map<Long, Node> parseNodes(String fileIn, Map<Long, Integer> nodeWayCounter) {
        Map<Long, Node> NodeMap = new HashMap<Long, Node>();
        try {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = null;

            reader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileIn));

        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                switch (startElement.getName().getLocalPart()) {
                    case "node":
                        //Load in neccesary attributes
                        Attribute id = startElement.getAttributeByName(new QName("id"));
                        Attribute lat = startElement.getAttributeByName(new QName("lat"));
                        Attribute lon = startElement.getAttributeByName(new QName("lon"));
                        if(id == null || lat == null || lon == null)
                        {
                            throw new XMLStreamException();
                        }

                        //Check if node is in the nodeWayCounter
                        long idLong = Long.parseLong(id.getValue());
                        if(nodeWayCounter.getOrDefault(idLong, 0)>0){
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
        }
        return NodeMap;
    }

    @Override
    public List<OsmWay> parseWays(String fileIn) {
        List<OsmWay> WayList = new ArrayList<OsmWay>();
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = null;

            reader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileIn));
            List<Long> nodeRefList = new ArrayList<Long>();
            Map<String, String> currentTags = new HashMap<String, String>();

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
                            WayList.add(new OsmWayImpl(nodeRefList, currentTags));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
