package openmap.standard;

import com.fasterxml.jackson.core.JsonGenerator;
import openmap.parsing.json.JsonGraphConstants;
import openmap.framework.Bounds;
import openmap.framework.Graph;
import openmap.framework.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphImpl implements Graph, Serializable {


    private Map<Long, Node> nodeMap;

    //Things we need to save
    private Bounds bounds;

    public GraphImpl(Map<Long, Node> nodeMap, Bounds bounds){
        this.nodeMap = nodeMap;
        this.bounds = bounds;
    }

    public GraphImpl(List<Node> nodeList, Bounds bounds){
        this.bounds = bounds;

        nodeMap = new HashMap<Long, Node>(nodeList.size());
        for (Node n : nodeList) {
            nodeMap.put(n.getId(), n);
        }
        doDeserialization();
    }

    public GraphImpl(JSONObject obj){
        this.bounds = new BoundsImpl((JSONObject)obj.get(JsonGraphConstants.GraphBounds));

        JSONArray jNodeArray = (JSONArray) obj.get(JsonGraphConstants.GraphNodes);
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
    public void doDeserialization(){
        //for (Map.Entry<Long, Node> entry : nodeMap.entrySet()) {  ((NodeImpl)entry.getValue()).convertPathDeserialization(nodeMap); }

        nodeMap.entrySet().parallelStream().forEach(entry -> ((NodeImpl)entry.getValue()).convertPathDeserialization(nodeMap));
    }

    @Override
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put(JsonGraphConstants.GraphBounds, bounds.getJSONObject());

        JSONArray jNodeArray = new JSONArray();

        //System.out.println(new BoundsImpl(bounds.getJSONObject()).getMinX());

        for (Map.Entry<Long, Node> mapEntry : nodeMap.entrySet()) {
            jNodeArray.add(mapEntry.getValue().getJSONObject());
        }

        obj.put(JsonGraphConstants.GraphNodes, jNodeArray);

        return obj;
    }

    @Override
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException {
        jGenerator.writeStartObject();
        bounds.WriteToJsonGenerator(jGenerator);

        jGenerator.writeArrayFieldStart(JsonGraphConstants.GraphNodes);
        for (Map.Entry<Long, Node> mapEntry : nodeMap.entrySet()) {
            mapEntry.getValue().WriteToJsonGenerator(jGenerator);
        }
        jGenerator.writeEndArray();

        jGenerator.writeEndObject();

    }
}
