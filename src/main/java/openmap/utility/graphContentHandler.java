package openmap.utility;


import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class graphContentHandler implements ContentHandler {


    @Override
    public boolean endArray() throws ParseException, IOException {
        System.out.println("inside endArray");
        return true;
    }
    @Override
    public void endJSON() throws ParseException, IOException {
        System.out.println("inside endJSON");
    }
    @Override
    public boolean endObject() throws ParseException, IOException {
        System.out.println("inside endObject");
        return true;
    }
    @Override
    public boolean endObjectEntry() throws ParseException, IOException {
        System.out.println("inside endObjectEntry");
        return true;
    }
    public boolean primitive(Object value) throws ParseException, IOException {
        System.out.println("inside primitive: " + value);
        return true;
    }
    @Override
    public boolean startArray() throws ParseException, IOException {
        System.out.println("inside startArray");
        return true;
    }
    @Override
    public void startJSON() throws ParseException, IOException {
        System.out.println("inside startJSON");
    }
    @Override
    public boolean startObject() throws ParseException, IOException {
        System.out.println("inside startObject");
        return true;
    }
    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
        System.out.println("inside startObjectEntry: " + key);
        return true;
    }
}
