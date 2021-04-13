package openmap.gui.framework;

import openmap.framework.Node;

import java.awt.*;
import java.util.List;

/**
 * Interface for a tile in a map implementation.
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 017-03-2021
 */
public interface MapTile {

    /**
     * Get the list of nodes in this tile
     * @return The list of nodes in tile
     */
    List<Node> getNodeList();

    /**
     * Add a node to this tile
     * @param n Node to add
     */
    void addNode(Node n);
}
