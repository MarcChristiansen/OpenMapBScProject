package openmap.JsonParsing;


import java.io.IOException;
import java.util.*;

import openmap.framework.Bounds;
import openmap.framework.Node;
import openmap.framework.Path;
import openmap.standard.BoundsImpl;
import openmap.standard.NodeImpl;
import openmap.standard.StandardPathImpl;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

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

            case JsonGraphConstants.GraphBounds:
                bounds = new BoundsImpl();
                bounds.setMinX((Double)objectMap.get(JsonGraphConstants.BoundsMinX));
                bounds.setMinY((Double)objectMap.get(JsonGraphConstants.BoundsMinY));
                bounds.setMaxX((Double)objectMap.get(JsonGraphConstants.BoundsMaxX));
                bounds.setMaxY((Double)objectMap.get(JsonGraphConstants.BoundsMaxY));
                break;

            case JsonGraphConstants.GraphNodes:
                nodeList.add(new NodeImpl((Long)objectMap.get(JsonGraphConstants.NodeId), (double)objectMap.get(JsonGraphConstants.NodeX), (double)objectMap.get(JsonGraphConstants.NodeY), pathList));
                pathList = new ArrayList<>();
                break;

            case JsonGraphConstants.NodePath:
                pathList.add(new StandardPathImpl((long)objectMap.get(JsonGraphConstants.PathDestId), (double)objectMap.get(JsonGraphConstants.PathWeight)));
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
