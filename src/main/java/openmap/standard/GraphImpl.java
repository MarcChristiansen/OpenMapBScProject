package openmap.standard;

import openmap.framework.Bounds;
import openmap.framework.Graph;
import openmap.framework.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphImpl implements Graph, Serializable {

    private static final String  jBounds = "Bounds";
    private static final String  jNodes = "Nodes";

    private Map<Long, Node> nodeMap;

    //Things we need to save
    private Bounds bounds;

    public GraphImpl(Map<Long, Node> nodeMap, Bounds bounds){
        this.nodeMap = nodeMap;
        this.bounds = bounds;
    }

    public GraphImpl(JSONObject obj){
        this.bounds = new BoundsImpl((JSONObject)obj.get(jBounds));

        JSONArray jNodeArray = (JSONArray) obj.get(jNodes);
        nodeMap = new HashMap<Long, Node>(jNodeArray.size());
        for (Object nodeObj : jNodeArray) {
            Node n = new NodeImpl((JSONObject) nodeObj);
            nodeMap.put(n.getId(), n);
        }

        doDeserialization();

    }

    @Override
    public Map<Long, Node> getNodeMap() {
        return nodeMap;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void prepareForSerialization(){
        for (Map.Entry<Long, Node> entry : nodeMap.entrySet()) {  ((NodeImpl)entry.getValue()).convertPathForSerialization(); }
    }

    @Override
    public void doDeserialization(){
        //for (Map.Entry<Long, Node> entry : nodeMap.entrySet()) {  ((NodeImpl)entry.getValue()).convertPathDeserialization(nodeMap); }

        nodeMap.entrySet().parallelStream().forEach(entry -> ((NodeImpl)entry.getValue()).convertPathDeserialization(nodeMap));
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(jBounds, bounds.getJSONObject());

        JSONArray jNodeArray = new JSONArray();

        //System.out.println(new BoundsImpl(bounds.getJSONObject()).getMinX());

        for (Map.Entry<Long, Node> mapEntry : nodeMap.entrySet()) {
            jNodeArray.add(mapEntry.getValue().getJSONObject());
        }

        obj.put(jNodes, jNodeArray);

        return obj;
    }
}
