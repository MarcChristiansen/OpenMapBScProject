package openmap.gui;

import openmap.framework.Node;
import openmap.gui.framework.MapTile;

import java.util.ArrayList;
import java.util.List;

/**
 * Somewhat legacy - replaced with quadTile. Still used in the base TileMapImpl
 * Simple tile implementation
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 017-03-2021
 */
public class MapTileImpl implements MapTile {

    List<Node> nodeList;

    /**
     * Initialize this tile
     */
    public MapTileImpl() {
        this.nodeList = new ArrayList<>();
    }


    @Override
    public List<Node> getNodeList() {
        return nodeList;
    }

    @Override
    public void addNode(Node n) {
        nodeList.add(n);
    }
}
