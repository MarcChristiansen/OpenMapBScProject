package openmap.utility;


import java.io.IOException;
import java.util.*;

import openmap.framework.Bounds;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.standard.BoundsImpl;
import openmap.standard.NodeImpl;
import openmap.standard.StandardPathImpl;
import org.eclipse.emf.common.util.BasicEList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.NodeList;

public class GraphContentHandler implements ContentHandler {
    Bounds bounds;
    List<Node> nodeList;
    List<Path> pathList;

    Deque<String> objectStack;

    Map<String, Object> objectMap;

    @Override
    public void startJSON() throws ParseException, IOException {
        nodeList = new ArrayList<>();

        objectMap = new HashMap<>();
        objectStack = new ArrayDeque<>();
        pathList = new ArrayList<>();
    }

    //Graph
    private static final String  jBounds = "Bounds";
    private static final String  jNodes = "Nodes";

    //Paths
    private static final String  jDestId = "d";
    private static final String  jWeight = "w";

    //Nodes
    private static final String  jX = "x";
    private static final String  jY = "y";
    private static final String  jId = "id";
    private static final String  jPaths = "p";

    //Bounds
    private static final String  jMinX = "jMinX";
    private static final String  jMinY = "jMinY";
    private static final String  jMaxX = "jMaxX";
    private static final String  jMaxY = "jMaxY";

    @Override
    public boolean endArray() throws ParseException, IOException {
        return true;
    }
    @Override
    public void endJSON() throws ParseException, IOException {

    }
    @Override
    public boolean endObject() throws ParseException, IOException {
        if(objectStack.isEmpty()) { return true; }
        switch(objectStack.peek()){

            case jBounds:
                bounds = new BoundsImpl();
                bounds.setMinX((Double)objectMap.get(jMinX));
                bounds.setMinY((Double)objectMap.get(jMinY));
                bounds.setMaxX((Double)objectMap.get(jMaxX));
                bounds.setMaxY((Double)objectMap.get(jMaxY));
                break;

            case jNodes :
                nodeList.add(new NodeImpl((Long)objectMap.get(jId), (double)objectMap.get(jX), (double)objectMap.get(jY), pathList));
                pathList = new ArrayList<>();
                break;

            case jPaths:
                pathList.add(new StandardPathImpl((long)objectMap.get(jDestId), (double)objectMap.get(jWeight)));
                break;
        }
        return true;
    }
    @Override
    public boolean endObjectEntry() throws ParseException, IOException {
        objectStack.pop();
        return true;
    }
    public boolean primitive(Object value) throws ParseException, IOException {
        objectMap.put(objectStack.peek(), value);
        return true;
    }
    @Override
    public boolean startArray() throws ParseException, IOException {
        //System.out.println("inside startArray");
        return true;
    }

    @Override
    public boolean startObject() throws ParseException, IOException {
        //System.out.println("inside startObject");
        return true;
    }
    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
        objectStack.push(key);
        return true;
    }

    public List<Node> getNodeList(){
        return nodeList;
    }

    public Bounds getBounds(){
        return bounds;
    }
}
