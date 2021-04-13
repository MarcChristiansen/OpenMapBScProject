package openmap.standard;

import openmap.framework.Node;
import openmap.framework.NodeWrapper;

import java.util.Comparator;

/**
 * NodeWrapper for use in path finders.
 *
 * Primary usage is in priority queues to ensure non-mutable state
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 9-02-2021
 */
public class NodeWrapperImpl implements NodeWrapper, Comparable<NodeWrapper> {

    private Node node;
    private double dist;

    /**
     * Initialize nodeWrapper
     * @param node The node to reference
     * @param dist The non mutable var to safe
     */
    public NodeWrapperImpl(Node node, double dist) {
        this.node = node;
        this.dist = dist;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public double getDist() {
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
