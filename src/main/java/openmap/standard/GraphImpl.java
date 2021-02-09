package openmap.standard;

import openmap.framework.Bounds;
import openmap.framework.Graph;
import openmap.framework.Node;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GraphImpl implements Graph, Serializable {

    private Map<Long, Node> nodeMap;
    private Bounds bounds;

    public GraphImpl(Map<Long, Node> nodeMap, Bounds bounds){
        this.nodeMap = nodeMap;
        this.bounds = bounds;
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
        for (Map.Entry<Long, Node> entry : nodeMap.entrySet()) {  ((NodeImpl)entry.getValue()).convertPathDeserialization(nodeMap); }
    }
}
