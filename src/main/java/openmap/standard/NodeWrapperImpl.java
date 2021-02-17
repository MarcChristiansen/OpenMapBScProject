package openmap.standard;

import openmap.framework.Node;
import openmap.framework.NodeWrapper;

import java.util.Comparator;

public class NodeWrapperImpl implements NodeWrapper, Comparable<NodeWrapper> {

    private Node node;
    private Double dist;

    public NodeWrapperImpl(Node node, Double dist) {
        this.node = node;
        this.dist = dist;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Double getDist() {
        return dist;
    }

    @Override
    public int compareTo(NodeWrapper o) {
        if(this.getDist() < o.getDist()){
            return -1;
        }
        if(this.getDist() > o.getDist()){
            return 1;
        }
        return 0;
    }
}
