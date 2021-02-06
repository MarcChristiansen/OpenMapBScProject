package openmap.standard;

import openmap.framework.Node;
import openmap.framework.OsmXmlParser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.InvalidPropertiesFormatException;


public class OsmXmlParserImpl implements OsmXmlParser {

    @Override
    public void parse(String fileIn) {
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
                        Attribute id = startElement.getAttributeByName(new QName("id"));
                        Attribute lat = startElement.getAttributeByName(new QName("lat"));
                        Attribute lon = startElement.getAttributeByName(new QName("lon"));
                        if(id == null || lat == null || lon == null)
                        {
                            throw new XMLStreamException();
                        }
                        Node node = new NodeImpl(Long.parseLong(id.getValue()),
                                                 Double.parseDouble(lat.getValue()),
                                                 Double.parseDouble(lon.getValue()));

                        //Todo remove print
                        System.out.println(id.getValue() + " lat: " + lat.getValue());
                        break;
                    case "way":
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
    }
}
